#Nuth Index Static Plugin for version 2.3


#About
A simple plugin called at indexing that adds fields with static data. You can specify a list of <fieldname>:<fieldcontent> per nutch job.
It can be useful when collections can't be created by urlpatterns, like in subcollection, but on a job-basis.

##Usage

1. Write this propery in nutch config
```
<property> 
  <name>index.static</name> 
  <value>source:nutch</value> 
</property> 
```

2. And include plugin in listi **plugin.includes**
```
<property>
  <name>plugin.includes</name>
 <value>protocol-httpclient|urlfilter-regex|parse-(html|tika)|index-(basic|anchor|static)|indexer-elastic|scoring-opic|urlnormalizer-(pass|regex|basic)</value>
 <description>Regular expression naming plugin directory names to
  include.  Any plugin not matching this expression is excluded.
  In any case you need at least include the nutch-extensionpoints plugin. By
  default Nutch includes crawling just HTML and plain text via HTTP,
  and basic indexing and search plugins. In order to use HTTPS please enable.
  protocol-httpclient, but be aware of possible intermittent problems with the.
  underlying commons-httpclient library.
  </description>
</property>
```

3. Rebuild project and Enjoy!


















































