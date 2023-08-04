# HTML Fragments using FreeMarker Example

This is an example to emulate the benefits of fragments using the Java [FreeMarker](https://freemarker.apache.org)
templating library. The main motivation to demonstrate this feature was to support use cases like
[htmx fragments](https://htmx.org/essays/template-fragments/).


### Macro components

In order to do conditional rendering, macros are used in a component-like style
(inspired by [React](https://react.dev)). 
By taking advantage of FreeMarker macros being defined at parse time, not at process time,
we can call them before their definition in the file.

***Example with macro definitions omitted***
```freemarker
    <!DOCTYPE html>
    <html>
    	<@Head />
    	<body>
    	    <@Menu />
    	    <@HeroBanner />
    	    <@Sidebar />
    	    <@MainContent />
    	    <@Footer />
    	</body>
    </html>
```


## Implementation

### Implementation crux

If you don't want to check the source, then all that is happening is that we optionally
add a FRAGMENT attribute to the model in the controller. The template then looks like this:
```freemarker
<#if FRAGMENT! == ''>
    <@Page />
<#elseif FRAGMENT == 'fragment1'>
    <@Fragment1 />
<#else>
    <#stop 'Unknown fragment identifier: "${FRAGMENT}"'>
</#if>

<#macro Fragment1>
    ... fragment content ...
</#macro>

<#macro Page>
    ... page content, possibly also invoking Fragment1 macro ...
</#macro>
```


### Alternative implementations + ideas

If you instead want the fragment lookup to be dynamic, rather than multiple if/else, you could do something like:
```freemarker
<#if FRAGMENT?has_content><@.vars[FRAGMENT] /><#else><@Page /></#if>
```
or if you still want to map the values and dislike if/else:
```freemarker
<#assign FRAGMENT_MACROS = {
    '': 'Page',
    'article': 'ArticleBlock'
} />
<@.vars[FRAGMENT_MACROS[FRAGMENT!]] />
```
Perhaps kebab-case to upper-camel-case translation would also be useful?  
(i.e. convert "my-fragment" to "MyFragment" so your macros can use the component naming style)
```freemarker
<#assign FRAGMENT = FRAGMENT!?replace('-', ' ')?capitalize?replace(' ', '') />
```

***Fragments only***

It may sometimes be desirable to have a template file which is just a collection of fragments
which are not part of a larger piece or where the larger piece is defined separately.  
```freemarker
<#if FRAGMENT == 'fragment1'>
    <@Fragment1 />
<#elseif FRAGMENT == 'fragment2'>
    <@Fragment2 />
<#else>
    <#stop 'Unknown or missing fragment identifier: "${FRAGMENT}"'>
</#if>
```
or maybe just:
```freemarker
<@.vars[FRAGMENT] />
```

## Running

This is built using Spring Boot and so to start the server, either:
- in your IDE, run the `main` method in `FreeMarkerFragmentsApplication`
- or, build the project using `./mvnw clean install` and then run the jar `java -jar target/fragments.jar`

***Pages***  
http://127.0.0.1:8080  
http://127.0.0.1:8080/fragment

http://127.0.0.1:8080/table  
http://127.0.0.1:8080/table/row  