package com.kipust.regex;

/**
 * A class to represent some value in the regex, either a Value or a Wildcard
 * Values have some actual string attached to them and Wildcards can accept any character
 */
public class Const {
    public static class Value extends Const{
        String value;
        public Value(String value){
            this.value = value;
        }

        @Override
        public String toString() {
            return  value;
        }
    }

    public static class Wildcard extends Const{
        public Wildcard(){

        }
        @Override
        public String toString(){
            return ".";
        }
    }
}
