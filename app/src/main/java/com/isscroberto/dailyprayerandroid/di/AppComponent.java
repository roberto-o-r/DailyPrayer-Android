package com.isscroberto.dailyprayerandroid.di;

import android.app.Application;

import com.isscroberto.dailyprayerandroid.DailyPrayerAndroid;
import com.isscroberto.dailyprayerandroid.di.modules.ActivityBindingModule;
import com.isscroberto.dailyprayerandroid.di.modules.ApplicationModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by roberto.orozco on 02/03/2018.
 */
@Singleton
@Component(modules = {ApplicationModule.class,
        ActivityBindingModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<DailyPrayerAndroid> {

    // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this).build().inject(this);
    // never having to instantiate any modules or say which module we are passing the application to.
    // Application will just be provided into our app graph now.
    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
