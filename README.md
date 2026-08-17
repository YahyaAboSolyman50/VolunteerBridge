Volunteer Bridge :
    Volunteer Bridge is an interactive Android platform designed specifically to organize and manage university student volunteering under the supervision of Dr. Lamia Al-Saedi, and the supervision of the Islamic University of Gaza (IUG),4
    connecting students with various institutions and organizations to facilitate joining volunteer opportunities and tracking completed hours.


Key Features :

  - Built with Jetpack Compose: Modern, smooth user interfaces supporting Dark and Light modes.
  
  - Clean Architecture & MVVM: A well-structured codebase ensuring high maintainability and scalability.
  
  - Smart JWT Authentication: Secure login flow with automated token refresh handled via OkHttpClient Interceptor & Authenticator.
  
  - Volunteer Hours Management: Track students' total volunteer hours and progress percentages through accurate visual indicators.
  
  - Opportunities Hub: Browse available volunteer opportunities, view detailed information, and apply instantly.
  
  - Real-time Notifications: Send and receive alerts between students and organizations regarding application updates.
  
  - Pull-to-Refresh: Smooth data synchronization directly from the backend server.



Tech Stack :

  - Language: Kotlin (100%)
  
  - UI Framework: Jetpack Compose, Material Design 3
  
  - Architecture: MVVM (Model-View-ViewModel)
  
  - Networking: Retrofit, OkHttp3 (with Interceptors & Authenticators for JWT management)
  
  - Concurrency & State: Coroutines & Flows




Project Structure : 

  com.example.volunteerbridge
  │
  ├── data/             # النماذج، طبقة الاتصال، وإدارة الـ Tokens
  ├── model/            # الفئات والنماذج الأساسية (Classes & Models)
  ├── network/          # إعدادات الـ Retrofit, Interceptors, and Authenticators
  ├── view/             # واجهات المستخدم (Jetpack Compose Screens)
  └── viewmodelApi/     # نماذج العرض (ViewModels) لإدارة منطق الشاشات والربط مع الـ API



Developed By :
  Yahya Abo Solyman - Information Technology Student at the Islamic University of Gaza.


Libraries: Shimmer (for loading placeholders)
