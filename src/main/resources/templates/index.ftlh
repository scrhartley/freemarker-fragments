<!DOCTYPE html>
<html>
    <head>
        <link rel="icon" href="data:;base64,iVBORw0KGgo=">
        <@Style />
    </head>
    <body>

        <h4>Simple page links</h4>
        <ul>
            <li><a href="/auto">/auto</a></li>
            <li><a href="/auto/fragment">/auto/fragment</a></li>
            <li><a href="/manual">/manual</a></li>
            <li><a href="/manual/fragment">/manual/fragment</a></li>
        </ul>

        <h4>Table links</h4>
        <ul>
            <li><a href="/auto/table">/auto/table</a></li>
            <li><a href="/auto/table/row">/auto/table/row</a></li>
            <li><a href="/manual/table">/manual/table</a></li>
            <li><a href="/manual/table/row">/manual/table/row</a></li>
        </ul>

    </body>
</html>
<#macro Style>
    <style>
        body {
            margin: 3em;
        }
        li {
            padding-bottom: 0.25em;
        }
        li:nth-child(2n) {
            padding-bottom: 0.4em;
        }
    </style>
</#macro>
