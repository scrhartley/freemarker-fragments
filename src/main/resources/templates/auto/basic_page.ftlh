<!DOCTYPE html>
<html>
    <@Head />
    <body>
        <h1>My page!</h1>
        <br />
        <article>
            <@ArticleBlock />
        </article>
        <br />
        <div>End of page</div>
    </body>
</html>

<#macro Head>
    <head>
        <link rel="icon" href="data:;base64,iVBORw0KGgo=">
        <style>
            body {
                max-width: 40rem;
                margin: auto;
            }
        </style>
    </head>
</#macro>

<#macro ArticleBlock>
    <p>
        Lorem ipsum dolor sit amet. Sit saepe culpa qui veniam praesentium ut dolor mollitia sed
        cupiditate accusantium. Quo repellat blanditiis ut ipsum dolore ad nobis odit
        et provident nesciunt ea omnis explicabo.
    </p>
</#macro>