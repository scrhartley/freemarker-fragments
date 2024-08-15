<#import "libs/autoFragmentImportNesting_lib.ftlh" as lib>
first line
<@lib.MacroCallingNested />
last line

<#macro LocalMacro1>
    local macro 1 first line<#lt>
    <@lib.MacroCallingNested />
    local macro 1 last line<#lt>
</#macro>