# Login Example with MVVM, DataBinding With LiveData.

This is a very simple **Login Example** using **MVVM pattern and DataBinding and LiveData** in Android. It takes input from the UI using DataBinding **"@="**, stores it in LiveData and displays back on the UI.

This example is for those who want to learn the easiest way to get data from UI. This is useful in many ways such as Saving Development Time, Code Reusability, Validations etc. No wonder it is being used all over Android Community.

So let's get started on using these technologies together in a single application:

1. What is MVVM? 
2. What is DataBinding?
3. What is LiveData?
4. Implementation Step-by-Step
5. Conclusion


## **What is MVVM?**

**Answer:** MVVM is a design pattern for organizing GUI applications that has become popular on Android.

This concept will introduce you to the main 3 components of MVVM, the View, Model, and ViewModel. 

The Model View ViewModel (MVVM) is an architectural pattern used in software engineering that originated from Microsoft which is specialized in the Presentation Model design pattern. It is based on the Model-view-controller pattern (MVC), and is targeted at modern UI development platforms (WPF and Silverlight) in which there is a UX developer who has different requirements than a more "traditional" developer. MVVM is a way of creating client applications that leverages core features of the WPF platform, allows for simple unit testing of application functionality, and helps developers and designers work together with less technical difficulties.

**View:** A View is defined in XAML and should not have any logic in the code-behind. It binds to the view-model by only using data binding.

**Model:** A Model is responsible for exposing data in a way that is easily consumable by WPF. It must implement INotifyPropertyChanged and/or INotifyCollectionChanged as appropriate.

**ViewModel:** A ViewModel is a model for a view in the application or we can say as abstraction of the view. It exposes data relevant to the view and exposes the behaviors for the views, usually with Commands.

**Model:**
- Definition, roles and responsibilities.
- What should go in your model layer and what shouldn't.
- Benefits of model isolation and how it affects testing.

**View:**
- Definition, roles and responsibilities.
- How it interacts with the ViewModel.

**ViewModel:**
- Definition, roles and responsibilities.
- How it supports the View, by providing actions and observable state.
- Interactions with the Model.
- Isolation from the View.

<img src="https://image.ibb.co/nmhxNK/2.png" alt="2" />


## **What is DataBinding?**

**Answer:** The Data Binding Library is a support library that allows you to bind UI components in your layouts to data sources in your app using a declarative format rather than programmatically and many more.

<img src="https://preview.ibb.co/idDKme/3.png" alt="3" />


## **What is LiveData?**

**Answer:** LiveData is an observable data holder class. Unlike a regular observable, LiveData is lifecycle-aware, meaning it respects the lifecycle of other app components, such as activities, fragments, or services. This awareness ensures LiveData only updates app component observers that are in an active lifecycle state.

Using LiveData provides the following advantages:

**Ensures your UI matches your data state:**
    LiveData follows the observer pattern. LiveData notifies Observer objects when the lifecycle state changes. You can consolidate your code to update the UI in these Observer objects. Instead of updating the UI every time the app data changes, your observer can update the UI every time there's a change.
    
**No memory leaks:**
    Observers are bound to Lifecycle objects and clean up after themselves when their associated lifecycle is destroyed.
    
**No crashes due to stopped activities:**
    If the observer's lifecycle is inactive, such as in the case of an activity in the back stack, then it doesn’t receive any LiveData events.
    
**No more manual lifecycle handling:**
    UI components just observe relevant data and don’t stop or resume observation. LiveData automatically manages all of this since it’s aware of the relevant lifecycle status changes while observing.
    
**Always up to date data:**
    If a lifecycle becomes inactive, it receives the latest data upon becoming active again. For example, an activity that was in the background receives the latest data right after it returns to the foreground.
    
**Proper configuration changes:**
    If an activity or fragment is recreated due to a configuration change, like device rotation, it immediately receives the latest available data.
    
**Sharing resources:**
    You can extend a LiveData object using the singleton pattern to wrap system services so that they can be shared in your app. The LiveData object connects to the system service once, and then any observer that needs the resource can just watch the LiveData object. For more information, see Extend LiveData. 
    
<img src="https://preview.ibb.co/bx0qRe/4.png" alt="4" />


## **Implementation Step-by-Step**

### **Step1:** Adding DataBinding and Implementations in your Gradle File:

```Java
android {
    ...
    dataBinding {
        enabled true
    }
}
```

```Java
def life_versions = "1.1.1"

// Lifecycle components
implementation "android.arch.lifecycle:extensions:$life_versions"
annotationProcessor "android.arch.lifecycle:compiler:$life_versions"
```


### **Step2:** Create a new class for the Model(LoginUser):

```Java

import android.util.Patterns;

public class LoginUser {

    private String strEmailAddress;
    private String strPassword;

    public LoginUser(String EmailAddress, String Password) {
        strEmailAddress = EmailAddress;
        strPassword = Password;
    }

    public String getStrEmailAddress() {
        return strEmailAddress;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public boolean isEmailValid() {
        return Patterns.EMAIL_ADDRESS.matcher(getStrEmailAddress()).matches();
    }


    public boolean isPasswordLengthGreaterThan5() {
        return getStrPassword().length() > 5;
    }

}
```


### **Step2:** Create a new class for the ViewModel(LoginViewModel):

```Java

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.view.View;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<String> EmailAddress = new MutableLiveData<>();
    public MutableLiveData<String> Password = new MutableLiveData<>();

    private MutableLiveData<LoginUser> userMutableLiveData;

    public MutableLiveData<LoginUser> getUser() {

        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        return userMutableLiveData;

    }

    public void onClick(View view) {

        LoginUser loginUser = new LoginUser(EmailAddress.getValue(), Password.getValue());

        userMutableLiveData.setValue(loginUser);

    }

}
```


### **Step3:** The View class(MainActivity):

```Java

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

public class MainActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        binding.setLifecycleOwner(this);

        binding.setLoginViewModel(loginViewModel);

        loginViewModel.getUser().observe(this, new Observer<LoginUser>() {
            @Override
            public void onChanged(@Nullable LoginUser loginUser) {

                if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrEmailAddress())) {
                    binding.txtEmailAddress.setError("Enter an E-Mail Address");
                    binding.txtEmailAddress.requestFocus();
                }
                else if (!loginUser.isEmailValid()) {
                    binding.txtEmailAddress.setError("Enter a Valid E-mail Address");
                    binding.txtEmailAddress.requestFocus();
                }
                else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrPassword())) {
                    binding.txtPassword.setError("Enter a Password");
                    binding.txtPassword.requestFocus();
                }
                else if (!loginUser.isPasswordLengthGreaterThan5()) {
                    binding.txtPassword.setError("Enter at least 6 Digit password");
                    binding.txtPassword.requestFocus();
                }
                else {
                    binding.lblEmailAnswer.setText(loginUser.getStrEmailAddress());
                    binding.lblPasswordAnswer.setText(loginUser.getStrPassword());
                }

            }
        });

    }
}
```


### **Finally:** The XML File: (Important Changes Here)

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="LoginViewModel"
            type="com.example.umangburman.databindingwithlivedata.ViewModel.LoginViewModel" />
    </data>

    <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:isScrollContainer="true">

        <android.support.constraint.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            tools:context=".View.MainActivity">

            <TextView
                android:id="@+id/lblTitle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginEnd="8dp"
                android:layout_marginStart="8dp"
                android:layout_marginTop="16dp"
                android:lineSpacingExtra="8sp"
                android:text="Login Example Using MVVM, DataBinding with LiveData"
                android:textAlignment="center"
                android:textSize="18sp"
                android:textStyle="bold"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent" />

            <EditText
                android:id="@+id/txtEmailAddress"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginEnd="32dp"
                android:layout_marginStart="32dp"
                android:layout_marginTop="32dp"
                android:ems="10"
                android:hint="E-Mail Address"
                android:inputType="textEmailAddress"
                android:text="@={LoginViewModel.EmailAddress}"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/lblTitle" />

            <EditText
                android:id="@+id/txtPassword"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginEnd="32dp"
                android:layout_marginStart="32dp"
                android:layout_marginTop="16dp"
                android:ems="10"
                android:hint="Password"
                android:inputType="textPassword"
                android:text="@={LoginViewModel.Password}"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/txtEmailAddress" />

            <Button
                android:id="@+id/btnLogin"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginEnd="32dp"
                android:layout_marginStart="32dp"
                android:layout_marginTop="32dp"
                android:text="Click to Login"
                android:onClick="@{(v) -> LoginViewModel.onClick(v)}"
                android:textSize="18sp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/txtPassword" />

            <TextView
                android:id="@+id/lblTagline"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginEnd="8dp"
                android:layout_marginStart="8dp"
                android:layout_marginTop="60dp"
                android:text="See the Results Below From LiveDataBinding"
                android:textColor="@android:color/background_dark"
                android:gravity="center"
                android:textSize="16sp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/btnLogin" />

            <TextView
                android:id="@+id/lblEmailAnswer"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginEnd="8dp"
                android:layout_marginStart="8dp"
                android:layout_marginTop="8dp"
                android:text="---"
                android:textColor="@android:color/holo_blue_light"
                android:textSize="20sp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/lblTagline" />

            <TextView
                android:id="@+id/lblPasswordAnswer"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginBottom="32dp"
                android:layout_marginEnd="8dp"
                android:layout_marginStart="8dp"
                android:layout_marginTop="8dp"
                android:text="---"
                android:textColor="@android:color/holo_blue_light"
                android:textSize="20sp"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/lblEmailAnswer" />

        </android.support.constraint.ConstraintLayout>

    </android.support.v4.widget.NestedScrollView>

</layout>
```

## **Conclusion**

Hopefully this guide should have helped you in making your tasks really easier in terms of many things, such as: eliminating **findViewById(...)** and many more.

Feel free to reach me at any time on [LinkedIn](https://www.linkedin.com/in/umangburman/)


