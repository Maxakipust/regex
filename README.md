## A Regex Engine
### Written by Max Kipust (maxakipust@gmail.com)

This is a very simple regex engine based off of Brzozowski Derivatives
It only implements a few operations, but those operations can be built into more complex ones
https://en.wikipedia.org/wiki/Brzozowski_derivative
***
### Grammar:

| Operation   | Grammer     | Description |
| ----------- | ----------- | ----------- |
| AND         | &(A,B)      | Matches against A followed by B |
| OR          | &#124;(A,B) | Matches against A or B |
| KLEENE STAR | *(A)        | Matches against A zero or more times |
| CONSTANT    | "abc"       | Matches against the constant "abc" |
| WILDCARD    | .           | Matches against any character |

***
### Using a regex:
In order to create a pattern you can use the Pattern.Compile method.
Then to match it against a string you can use the match method.

    Pattern p = Pattern.Compile("&(*("a"), |("b","c"))");
    Dfa.DFAResult result = p.match("aaaaaab");

### Viewing the results
The result of a match is a DFAResult class.
It will either be an instance of `AcceptingResult` or `TrashResult`. 
If it is an instance of `AcceptingResult` then that means that the string matched the patter to completion.
If it is an instance of `TrashResult` then that means that the string failed to match the pattern in some way.
You can get the exact point where it failed in the string in addition to possible tokens that could have been used where it failed to allow it to take another step.