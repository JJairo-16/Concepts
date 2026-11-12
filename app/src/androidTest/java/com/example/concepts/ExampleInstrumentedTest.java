package com.example.concepts;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/** Contains an on-device smoke test for the application's Android context. */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    /** Checks that the target context exposes the Concepts application package name. */
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.concepts", appContext.getPackageName());
    }
}
