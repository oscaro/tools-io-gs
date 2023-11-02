# tools-io-gs

Google Storage support for [`tools.io`](https://github.com/oscaro/tools-io).

## Usage

```clojure
[com.oscaro/tools-io-gs  "0.X.X"] ; See (clojars release)
```

[API Docs](https://cljdoc.org/d/com.oscaro/tools-io-gs/0.1.6/doc/readme).

### Example

```clojure
;read json
(doseq [x (read-jsons-file "gs://bucket/sample.jsons.gz")]
  (println x))

; list files
(doseq [filename (list-files "gs://bucket/mydir/")]
  (println filename))

; -> output:
; gs://bucket/mydir/example/raw-animals-aaaaaaaaaa.jsons.gz
; gs://bucket/mydir/example/raw-animals-aaaaaaaaab.jsons.gz
; gs://bucket/mydir/example/raw-animals-aaaaaaaaac.jsons.gz
; gs://bucket/mydir/example/raw-animals-aaaaaaaaad.jsons.gz
; gs://bucket/mydir/example/raw-animals-aaaaaaaaae.jsons.gz

; list files in a (virtual) directory
(doseq [filename (list-files "gs://bucket/mydir/" {:current-directory true})]
  (println filename))

; -> output:
; gs://bucket/mydir/example/

; Get folder/file size

(sizeof "gs:/foo/bar") ;; => 8293

```

## License

Copyright © 2016-2023 Oscaro.com

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
