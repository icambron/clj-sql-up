# clj-sql-up
A Leiningen plugin to manage SQL database migrations simply and dynamically with clojure/jdbc.

## Features
* Database agnostic (migrations are created from sequences of sql strings)
* Supports creation of stored procedures / other complicated sql statements
* Runs within clojure so sql strings can easily be dynamically constructed
* Has a simple `create` command for generating migration files

## Installation
#### In your project.clj file:

Put clj-sql-up into your plugins vector:
```clojure
:plugins [[clj-sql-up "0.1.0"]]
```

Add database connection info (both your driver and jdbc connection string):

```clojure
:clj-sql-up {:database "jdbc:postgresql://localhost:5432/foo?foo"
             :deps [[org.postgresql/postgresql "9.2-1003-jdbc4"]]}
;; OR			 
:clj-sql-up {:database {:subprotocol "mysql"
                        :subname "//localhost:3306/foo"
                        :user "foo"
                        :password ""}
             :deps [[mysql/mysql-connector-java "5.1.6"]]}
```		 

## Usage
Basic usage (though it doesn't get much more complicated):

    $ lein clj-sql-up create create-posts

```clojure
;; migrations/20130714150641624-create-posts.clj

;; Note: the generated methods would just return a blank vector
;;       Just inserting some statements as an example
(defn up []
  ["CREATE TABLE foo(id int)"
   "CREATE INDEX foo_index ON foo(id)"])

(defn down []
  ["drop table foo"])
```
	$ lein clj-sql-up migrate
	Migrating: 20130714150634587-create-timestamps-fn.clj
	Migrating: 20130714150641624-create-posts.clj
	$ lein clj-sql-up rollback
	Reversing: 20130714150641624-create-posts.clj	

## TODO	
* Add an environment flag to support mult. databases (for testing, etc)
* Write more tests
* Clean up some of the general structure / duplication in migrate.clj
* Make certain aspects more generic (specifying migrations dir, etc.)
* Ensure compatibility with jdbc-supported databases

## Motivation
This library was inspired by weavejester's [ragtime](https://github.com/weavejester/ragtime/) library (and uses a few code snippets from that project).  The motivation for starting a new project was to make things a bit more in line structurally with other well-known migration tools and easily allow for creation of more complicated sql commands (eg: stored procedures/functions).  Also was a great opportunity to learn about leiningen plugins :)

## License
Copyright © 2013 Christopher Kuttruff

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html), the same as Clojure.
