# Datum Brain — Web Fetcher


It fetches web page from the given URL and parses it and saves it's output in a *HTML* file that can be opened in a web browser.

It currently shows following and their count:
- Headings (h1, h2, h3)
- Inbound links
- Outbound links
- Images


#### Known Bugs:
- Following inbound links are shown as outbound links
  - links starting without domain, e.g. `/home`
  - `www.google.com` -> `www.google.com.pk`
  
