#!/bin/bash
javalc=`find . -iname *.java -exec cat {} + | wc -l`
echo "Number of lines of java code: "$javalc
xmllc=`find . -iname *.xml -exec cat {} + | wc -l`
echo "Number of lines of xml code: "$xmllc
echo "Total lines of code in project: "$(( $javalc+$xmllc ))
