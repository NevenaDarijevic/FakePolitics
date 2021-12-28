# FakePolitics


FakePolitics is a blog that contains articles from various portals categorized according to truthfulness and credibility. In addition to reviewing articles, readers are also allowed to leave comments on them, as well as to report fake news. The blog administrator is able to add, edit or delete new items. Also, the admin is allowed to view statistics, as well as view the reported content. The admin analyzes the submitted articles and decides whether to add them to the blog and with which tag (true or false).


## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.
The application uses following libraries:
[Compojure][]
[Ring][]
[Monger][]
[Hiccup][]

[leiningen]: https://github.com/technomancy/leiningen
[compojure]: https://github.com/weavejester/compojure
[ring]: https://github.com/ring-clojure/ring
[monger]: https://github.com/michaelklishin/monger
[hiccup]: https://github.com/weavejester/hiccup

## Running

To start a web server for the application, run:

    lein ring server

## References

Daniel Higginbotham (2015), [Clojure for the Brave and True][]

[clojure for the Brave and True]: https://www.braveclojure.com/clojure-for-the-brave-and-true/ 

## License

Copyright © 2021 Nevena Darijevic
