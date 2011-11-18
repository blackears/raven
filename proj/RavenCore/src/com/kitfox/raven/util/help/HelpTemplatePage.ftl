[#ftl]
[#setting number_format="computer"]
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="author" content="[#if author??]${author}[/#if]"/>
        <meta name="date" content="[#if date??]${date?date}[/#if]"/>
        <meta name="description" content="Raven Editor Documentation"/>
        <meta name="keywords" content="raven editor"/>
        <title>${page.title}</title>
        <link rel="stylesheet" type="text/css" href="${page.cssPath}"/>
    </head>
    <body>
        <div class="header"><h1>${page.title}</h1>
        [#list page.navBar as nav]<a href="${nav.url}"><span class="button">${nav.name}</span></a> [/#list]
        </div>
        <hr/>

        ${page.body}
        <hr/>
    </body>
</html>
