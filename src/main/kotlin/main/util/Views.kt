package main.util

class Views {
    interface Minimal
    class UserViews {
        interface UserComplete : Minimal
    }
    class ResultViews {
        interface ResultComplete : Minimal
    }
}