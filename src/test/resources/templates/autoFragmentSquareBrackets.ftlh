first line
[@Macro2 /]
[#import "libs/autoFragmentSquareBrackets_lib.ftlh" as lib]
[@lib.LibMacro3 /]
last line

[#macro Macro1]
    macro 1[#lt]
    [@lib.LibMacro3 /]
[/#macro]
[#macro Macro2]
    macro 2[#lt]
[/#macro]