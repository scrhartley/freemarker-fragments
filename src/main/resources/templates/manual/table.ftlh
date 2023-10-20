<#if FRAGMENT?has_content>
    <#if FRAGMENT == 'row'>
        <@Row />
    <#else>
        <#stop 'Unknown fragment identifier: "${FRAGMENT}"'>
    </#if>
<#else>
    <!DOCTYPE html>
    <html>
        <@Head />
        <body><@Table /></body>
    </html>
</#if>

<#-- If a person isn't passed as a parameter,
     then default to person defined in the model -->
<#macro Row person=person>
    <tr>
        <td>${person.id}</td>
        <td>${person.name}</td>
        <td>${person.email}</td>
    </tr>
</#macro>

<#macro Table>
    <table width="100%">
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Email</th>
        </tr>
        <#list people as p>
            <@Row person=p/>
        </#list>
    </table>
</#macro>

<#macro Head>
    <head>
        <link rel="icon" href="data:;base64,iVBORw0KGgo=">
        <style>
            body {
                max-width: 30rem;
                margin: auto;
            }
            th {
                text-align: left;
            }
        </style>
    </head>
</#macro>
