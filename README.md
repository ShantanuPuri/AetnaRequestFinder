OBJECTIVE: To visit each Medical CPB in Aetna's clinical policy bulletin to retrieve the following information:

1. Medical CPB Title
2. Number
4. Last Review Date
5. Effective Date
6. Next Review Date
7. Background
3. Policy 
9. CPT code number
10. CPT code description
11. CPT code cover
12. CPT code PA required?
13. HCPCS code number
14. HCPCS code description
15. HCPCS code cover
16. ICD-10 code number
17. ICD-10 code description
18. ICD-10 code cover


TO RUN: 
1. Configure the build path and add the external jars provided in the library folder 
   (majority of these jars are required for Selenium to work, a couple for Jsoup)
2. In the first line of the SetUp method in Selenium.java, change the second parameter to match the location 
   of the gecko driver file, specific to your system 
3. Install the latest version of Firefox - 60.0.2, released on June 6th, 2018
   LINK -> https://www.mozilla.org/en-US/firefox/60.0.2/releasenotes/
4. Install the latest version of Selnium (compatible with Java) - 3.12.0, released on May 8th, 2018
   LINK -> https://docs.seleniumhq.org/download/ 