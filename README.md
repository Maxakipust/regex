# regex
A very basic regex engine

as of right now the syntax looks like:
```
r -> *(r)
r -> |(a,b)
r -> &(a,b)
r -> "anything"
r -> [a-z]
```
use "" to match the empty string


The end goal is to be able to generate a DFA from a regex using regex derivatives and then evaluate the DFA. I may also try the less efficent approach of converting the regex to an NFA and then the NFA to a DFA. 