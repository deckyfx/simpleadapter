# Simple Adapter
[![](https://jitpack.io/v/deckyfx/simpleadapter.svg)](https://jitpack.io/#deckyfx/simpleadapter)

Adapterview made easy

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
	repositories {
	...
		maven { url 'https://jitpack.io' }
	}
}
```
Add the dependency

```gradle
dependencies {
	compile 'com.google.code.gson:gson:2.8.1'
	compile 'com.github.deckyfx:simpleadapter:0.25@aar'
	// or if always get the latest
	compile 'com.github.deckyfx:simpleadapter:-SNAPSHOT'	
}
```

## Sample Code


extends **JSONParserAdapter** to your liking
```java

class MyParser extends JSONParserAdapter{
    @Override
    public void init() {
        // init the parser
    }

    @Override
    public Object fromJson(String json, Class<? extends BaseItem> klas) {
        //return parser deserialize result;
    }

    @Override
    public Object fromJsonArray(String json, Class<? extends BaseItem[]> klas) {
        //return parser deserialize result;
    }

    @Override
    public String toJson(BaseItem baseItem) {
        //return parser serialize result;
    }
}
```

And in your listview activity
```java
...
MyParser parser = New MyParser();
BaseItem.setJSONParser(parser);

private AdapterDataSet<AdapterItem> dataset = new AdapterDataSet<AdapterItem>();
private SimpleAdapter<AdapterItem> adapter;
...

```

```java
...
adapter = new SimpleAdapter<AdapterItem>(this, dataset);
listview.setAdapter(adapter);
dataset.add(new AdapterItem("Item 1"));
dataset.add(new AdapterItem("Item 2"));
adapter.notifyDataSetChange();
...

```

More sample is [here]

## Feature:

 * List View Adapter
 * Expandable List View Adapter
 * Recycle View Adapter
 * Using Gson to parse JSON
 * Parse date from string / viceversa
 * Listen to item view onclick, ontouch, and onbindview
