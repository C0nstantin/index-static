/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.indexer.staticfield;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.avro.util.Utf8;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.storage.WebPage;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;

import java.lang.CharSequence;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * A simple plugin called at indexing that adds fields with static data. You can
 * specify a list of fieldname:fieldcontent per nutch job. It can be useful when
 * collections can't be created by urlpatterns, like in subcollection, but on a
 * job-basis.
 */

public class StaticFieldIndexer implements IndexingFilter {
  private Configuration conf;
  private HashMap<String, String[]> fields;
  private boolean addStaticFields = false;
  private String fieldSep = ",";
  private String kevSep = ":";
  private String valueSep = " ";

private static final Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

  static {
    FIELDS.add(WebPage.Field.INLINKS);
  }
/**
   * The {@link AnchorIndexingFilter} filter object which supports boolean
   * configuration settings for the deduplication of anchors. See
   * {@code anchorIndexingFilter.deduplicate} in nutch-default.xml.
   *
   * @param doc
   *          The {@link NutchDocument} object
   * @param url
   *          URL to be filtered for anchor text
   * @param page
   *          {@link WebPage} object relative to the URL
   * @return filtered NutchDocument
   */
  @Override
  public NutchDocument filter(NutchDocument doc,  String url,
      WebPage page) throws IndexingException {

    if (this.addStaticFields == true) {
      for (Entry<String, String[]> entry : this.fields.entrySet()) {
        for (String val : entry.getValue()) {
          doc.add(entry.getKey(), val);
        }
      }
    }
    return doc;
  }


/**
   * Gets all the fields for a given {@link WebPage} Many datastores need to
   * setup the mapreduce job by specifying the fields needed. All extensions
   * that work on WebPage are able to specify what fields they need.
   */
  @Override
  public Collection<WebPage.Field> getFields() {
    return FIELDS;
  }

  /**
   * Populate a HashMap from a list of fieldname:fieldcontent. See
   * {@index.static} in nutch-default.xml.
   *
   * @param fieldsString
   *          string containing field:value pairs
   * @return HashMap of fields and their corresponding values
   */
  private HashMap<String, String[]> parseFields(String fieldsString) {
    HashMap<String, String[]> fields = new HashMap<String, String[]>();

    /*
     * The format is very easy, it's a comma-separated list of fields in the
     * form <name>:<value>
     */
    for (String field : fieldsString.split(this.fieldSep)) {
      String[] entry = field.split(this.kevSep);
      if (entry.length == 2)
        fields.put(entry[0].trim(), entry[1].trim().split(this.valueSep));
    }

    return fields;
  }

  /**
   * Set the {@link Configuration} object
   */
  public void setConf(Configuration conf) {
    this.conf = conf;

    // NUTCH-2052: Allow user-defined delimiters in index.static
    this.fieldSep = this.regexEscape(conf.get("index.static.fieldsep", ","));
    this.kevSep = this.regexEscape(conf.get("index.static.keysep", ":"));
    this.valueSep = this.regexEscape(conf.get("index.static.valuesep", " "));

    String fieldsString = conf.get("index.static", null);
    if (fieldsString != null) {
      this.addStaticFields = true;
      this.fields = parseFields(fieldsString);
    }
  }

  /**
   * Get the {@link Configuration} object
   */
  public Configuration getConf() {
    return this.conf;
  }

  /**
   * Escapes any character that needs escaping so it can be used in a regexp.
   */
  protected String regexEscape(String in) {
    String result = in;
    if (in != null) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < in.length(); i++) {
        CharSequence c = in.subSequence(i, i+1);
        if ("<([{\\^-=$!|]})?*+.>".contains(c)) {
          sb.append('\\');
        }
        sb.append(c);
      }
      result = sb.toString();
    }
    return result;
  }
}
