xquery version "1.0-ml";

(: Namespace pattern must be:  
 : "http://marklogic.com/rest-api/resource/{$rname}" 
 : and prefix must match resource name :)
module namespace example =
  "http://marklogic.com/rest-api/resource/example";

declare default function namespace
  "http://www.w3.org/2005/xpath-functions";
  declare namespace roxy = "http://marklogic.com/roxy";
  
  declare namespace rapi="http://marklogic.com/rest-api";
declare option xdmp:mapping "false";

(: Conventions: 
 : Module prefix must match resource name, 
 : and function signatures must conform to examples below.
 : The $context map carries state between the extension
 : framework and the extension.
 : The $params map contains parameters set by the caller,
 : for access by the extension.
 :)

(: Function responding to GET method - must use local name 'get':)
declare function example:get(
    $context as map:map,
    $params  as map:map
) as document-node()*
{
    (: set 'output-type', used to generate content-type header :)
    let $output-type :=
        map:put($context,"output-type","application/xml") 
    let $arg1 := map:get($params,"arg1")
    let $content := 
        <args>
            {for $arg in $arg1
             return <arg1>{$arg1}</arg1>
            }
        </args>
    return document { $content } 
    (: must return document node(s) :)
};

(: Function responding to PUT method - must use local name 'put'. :)
declare function example:put(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()?
{
    (: get 'input-types' to use in content negotiation :)
    let $input-types := map:get($context,"input-types")
    let $negotiate := 
        if ($input-types = "application/xml")
        then () (: process, insert/update :) 
        else error((),"ACK",
          "Invalid type, accepts application/xml only") 
    return document { "Done"}  (: may return a document node :)
};

(: Function responding to POST method - must use local name 'post'. :)
declare
%roxy:params("name=xs:string", "file=xs:string", "message=xs:string?")
%rapi:transaction-mode('update')
 function example:post(
    $context as map:map,
    $params  as map:map,
    $input   as document-node()*
) as document-node()*
{
    let $docID := map:get($params, 'docID')
   let $revisionID := map:get($params, 'revisionID')
   let $versionID := map:get($params, 'versionID')
let $docStatus := map:get($params, 'docStatus')
let $pid := map:get($params, "pid")
let $file  := map:get($params, "file")
return 
xdmp:log($params)
};

(: Func responding to DELETE method - must use local name 'delete'. :)
declare function example:delete(
    $context as map:map,
    $params  as map:map
) as document-node()?
{
    xdmp:log("delete!")
};