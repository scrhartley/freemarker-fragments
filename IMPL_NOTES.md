### Implementation crux (no automation / manual)

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

***Automating***

Rather than having to handle calling the correct fragment macro at the top of each page,you could use
`Configuration.addAutoInclude` (or the setting `auto_include`) to reference a special template to be included at the
beginning of each template. This could use a convention to look for a specifically named macro if a fragment is
not set.
```freemarker
<#if FRAGMENT?has_content><@.vars[FRAGMENT] /><#elseif Page??><@Page /></#if>
```
Another option for the auto-included template is to contain a single macro which can be manually invoked by templates
which use fragments. If you're considering that, it may be nicer to instead use `Configuration.setSharedVariable`
and define a custom directive in Java
(see [TemplateDirectiveModel](https://freemarker.apache.org/docs/pgui_datamodel_directive.html)).
```freemarker
<#macro autoInvoke primary=Page!>
    <#if FRAGMENT?has_content><@.vars[FRAGMENT] /><#else><@primary /></#if>
</#macro>
```