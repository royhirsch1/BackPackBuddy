# Course Freak <img align="right" src="https://user-images.githubusercontent.com/38239587/51712293-f6a76880-2036-11e9-9c59-d73f5930cdc2.png" width="80">

<a style="margin-bottom: 0;" href='https://play.google.com/store/apps/details?id=com.coursefreak.app'><img align="right" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width="200px"/></a>


CourseFreak is an Android app for students. It is in fact a unified platform for many course-related tasks that can easily be done through the app's interactive environment.

![screnshot](https://user-images.githubusercontent.com/38239587/51712204-b1833680-2036-11e9-8b59-a50e34b0f3df.png)
Users can read and write **reviews**, Look for a partner and **get notifications** when new options arise.

# Recommender Engine
The app offers its users **personalized content** according to their preferences by producing **recommendations** for them, based on their liking.
Our ML engine is a simple [Collaborative Filter](https://en.wikipedia.org/wiki/Collaborative_filtering) we implmented in java, and you can find the code
[here](https://github.com/Technion236503/2019a-CourseFreak/blob/master/app/src/main/java/com/coursefreak/app/utils/Recommender.java).

![Collaborative Filtering](https://user-images.githubusercontent.com/38239587/51709643-9eb93380-202f-11e9-8ff2-da1329f6b202.JPG)

# Tools

The app was built with [Android Studio](https://developer.android.com/studio/) (Java) and uses [Firebase](https://firebase.google.com/) for user authentication, database and [FCM](https://firebase.google.com/docs/cloud-messaging/).
It was designed based on the priciples of [Material Design](https://material.io/design/) and was prototyped with [adobe XD](https://www.adobe.com/products/xd.html) with the [Zeplin](https://zeplin.io/) plugin.
The course data we used was downloaded and reformatted from Technion CS faculty's [website](http://www.cs.technion.ac.il/courses/all/by-number/) using Google sheet XML scripts and [XPATH Generator](https://chrome.google.com/webstore/detail/xpath-generator/dphfifdfpfabhbkghlmnkkdghbmocfeb) Chrome Extension.

Feel free to contact us if you have any further questions or suggestions.   


Team Members
=======
[Amit Sdeor](https://github.com/amso100)

[Roy Hirsch](https://github.com/royhirsch1)

[Denis Nastic](https://github.com/DxxN96)

Acknowledgments
=======

We leveraged these 3rd party libraries:

[Awesome Splash](https://github.com/ViksaaSkool/AwesomeSplash)

[Circular Image View](https://github.com/hdodenhof/CircleImageView)

License
=======

	Copyright 2019 Choice Freak
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    

