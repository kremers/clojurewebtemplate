(defproject clojurewebtemplate "1.0.0-SNAPSHOT"
  :description "kickstart a clojure web application"
  :url "http://www.martinkremers.de"
  :license {:name "MIT License"
            :url "http://www.martinkremers.de"}
  :dependencies [[org.clojure/clojure "1.5.1"]

                 ; Logging
                 [org.clojure/tools.logging "0.2.6"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [org.slf4j/jcl-over-slf4j "1.7.5"]
                 [ch.qos.logback/logback-classic "1.0.13"]

                 ; Web
                 [ring "1.2.1"]
                 [http-kit "2.1.13"]
                 [compojure "1.1.6"]
                 [com.cemerick/friend "0.2.0"]
                 [stencil "0.3.2"]
                 
                 ;File System Utilities
                 [fs "1.3.3"]

                 ;Utilities
                 [cheshire "5.2.0"]
                 [clj-time "0.6.0"]
                 [slingshot "0.10.3"]
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/data.zip "0.1.1"]

                 ]

  :jvm-opts ["-Djava.awt.headless=true"
             "-XX:+UseConcMarkSweepGC"
             "-XX:+UseParNewGC"
             "-XX:ParallelCMSThreads=4"
             "-XX:+ExplicitGCInvokesConcurrent"
             "-XX:+CMSParallelRemarkEnabled"
             "-XX:-CMSIncrementalPacing"
             "-XX:+UseCMSInitiatingOccupancyOnly"
             "-XX:CMSIncrementalDutyCycle=100"
             "-XX:CMSInitiatingOccupancyFraction=90"
             "-XX:CMSIncrementalSafetyFactor=10"
             "-XX:+CMSClassUnloadingEnabled"
             "-XX:+DoEscapeAnalysis"
             "-server"
             "-Xmx1g"
             "-d64"
             "-dsa"
             "-da"
             ]
  :properties { :project.build.sourceEncoding "UTF-8"}
  :aot [clojurewebtemplate.core]
  :main clojurewebtemplate.core
)
