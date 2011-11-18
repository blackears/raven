[#ftl]
[#setting number_format="computer"]

[#macro printToc index root]
    [#if index.children?size > 0]
        <ul class="contents">
        [#list index.children as entry]
            <li><a href="${entry.getRelPath(root.dest, entry.dest)}">${entry.title}</a></li>
            [@printToc index=entry root=root/]
        [/#list]
        </ul>
    [/#if]
[/#macro]

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

        [@printToc index=page root=page/]
        <hr/>
    </body>
</html>
