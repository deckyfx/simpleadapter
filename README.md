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
	compile 'com.github.deckyfx:simpleadapter:0.14'
}
```

## Sample Code

Create class represent your list item
```java
public class Book extends BaseItem {
	public String title;    
    public String author;
    
    public Book(String t, String a){
    	title = a;
        author = a;
    }
    
    public static class ViewHolder extends AdapterItem.ViewHolder {
    	/* Declare your view items here */
    	private TextView titleText, authorText;
        
        public ViewHolder(View convertView) {
            /* Initialize your view items here */
            titleText = convertView.findViewById(/* your view item id */);
            authorText = convertView.findViewById(/* your view item id */);
        }
    }
    
    public void setupView(Context ctx, int position, BaseItem itemobject) {
      	/* Setup your view items here */
        Book item = (Book) itemobject;
        titleText.setText(item.title);
        authorText.setText(item.author);
    }
}
```

And in your listview activity
```java
...
private AdapterDataSet<Book> dataset = new AdapterDataSet<Book>();
private SimpleAdapter<Book> adapter = new Adapter<Book>();
...

```

```java
...
adapter = new SimpleAdapter<Book>(this, dataset, R.layout.itemview, Book.ViewHolder);
listview.setAdapter(adapter);
dataset.add(new Book("Dragon Ball", "Akira Toriyama"));
dataset.add(new Book("Doraemon", "Fujiko F Fujio"));
adapter.notifyDataSetChange();
...

```

## Feature:

 * List View Adapter
 * Expandable List View Adapter
 * Recycle View Adapter
 * Using Gson to parse JSON
 * Parse date from string / viceversa
 * Listen to item view onclick, ontouch, and onbindview
 * 
