# Datum Brain — Web Fetcher


It fetches web page from the given URL, parses it, count the number of in-bound and out-bound links and then stores its output in a JSON file.

#### Known Bugs:
- Following inbound links are shown as outbound links
  - links starting without domain, e.g. `/home`
  - `www.google.com` -> `www.google.com.pk`
  
