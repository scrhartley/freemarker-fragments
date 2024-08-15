<#import "autoFragmentImport_lib.ftlh" as nested>

<#macro MacroCallingNested>
    macro calling nested first line<#lt>
    <@nested.LibMacro1 />
    macro calling nested last line<#lt>
</#macro>