# Bare Servlet

This project is the bare servlet implementation for assignment 1. Everything is kept at a bare minimum for fastest performance.

The servlet does 3 things to POST requests matching pattern `/swipe/*`:

1. Check if pathInfo is valid according to specs given. Path should be in `/swipe/[left|right][/]` format.
2. Check if JSON payload is deserializable into `SwipeDetails` using Gson.
3. Return corresponding response code and optional error message.
