# Covid-19 india dashboard with data-api

This is springboot-bootstrap application to show the dashboard for covid-19 cases in India. It shows below details

  - Total active, cured , deaths and migrated 
  - Bar chart for top ten states based on deaths, active cases, cured
  - India map with states where every state showing no of active/cured/deaths 
  - news feed related to covid-19 india
  - State wise cases with showing active cases in every district of the state
  - Top ten districts with highest no of active cases

# Demo
The app is deployed on gooogle cloud and can be access via url:

  - will be available soon
  
# How to setup
```git
 git clone https://github.com/ramveer93/covid-india.git
  ```
  ```mvn
  mvn clean install
  ```
 Import the folder to eclipse/intellij/sts as maven project
 # Swagger documents
You can access the swagger documentation for this on url:
- localhost:8080
- 

This text you see here is *actually* written in Markdown! To get a feel for Markdown's syntax, type some text into the left window and watch the results in the right.

### Tech
This project uses a number of open tech-stack to work properly:
* Java
* Bootstrap
* postgresql
* gcp and google sql to deploy it
* maven 
* js 
* zingchart
* newsapi.org to collect the news 
* https://www.mygov.in/corona-data/covid19-statewise-status data source 
