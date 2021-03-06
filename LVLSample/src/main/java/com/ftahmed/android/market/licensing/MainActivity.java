/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ftahmed.android.market.licensing;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.APKExpansionPolicy;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.util.HttpBin;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Welcome to the world of Android Market licensing. We're so glad to have you
 * onboard!
 * <p>
 * The first thing you need to do is get your hands on your public key.
 * Update the BASE64_PUBLIC_KEY constant below with your encoded public key,
 * which you can find on the
 * <a href="http://market.android.com/publish/editProfile">Edit Profile</a>
 * page of the Market publisher site.
 * <p>
 * Log in with the same account on your Cupcake (1.5) or higher phone or
 * your FroYo (2.2) emulator with the Google add-ons installed. Change the
 * test response on the Edit Profile page, press Save, and see how this
 * application responds when you check your license.
 * <p>
 * After you get this sample running, peruse the
 * <a href="http://developer.android.com/guide/publishing/licensing.html">
 * licensing documentation.</a>
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    // LVLSample Free 
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjc1OHAm6GqSs3X+gJX3PefqYQ9IUFzrbvtw+Y5qJGNFOGmkh/WpanjIiXxPbzzaQopYpKWj+bDFa6q4LOoS12h43S+T7ebW4CC3iWjWd2q43OEuQAN4wvFNG4JIefifak3jBrfvFlYC5WwKBGYMeN2aQN3dq4ILmJ49wy+69vWixTXNt2SXrQrc2sazcp1+6O718EoWYHo6rX3EiHiSy7+Kn1wpg14+hGVVYQhi2RlOzUA5dLu2exzAylzt0eG4eDNLNmQdR+E5RFz8hgOn+Tocfg/hCK/WgrkZYWQjdRuSpKRP67WT6yOnCsEZ2Ff7q4jfLqZuAk3BibpTAA31oUQIDAQAB";
    // LVLSample Paid
//    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAizieCgcd0bl32asBBuzGao0B1uNyFugDWPhSeN6pLlJz7eQKtmeBiAhccwyRmzcMJG9Y4Y8LM0SKGUI+7JDWFcF6yA/8Csrxkzp31Sbz4DL1+lZuSQ5TsGfqfu0nhzraeFqq1vqXaYYxuSvadVQlpUA32kV6qwWAFzshns25I38uUyOTGt3IvF1qntL+wax2Vb9OTnznOP+tNKA/zXgqdR+27nWfOhJQ9CNkNE26lXbNEdTdQCBBhZ33H0zx5Ua982vBPjekX3Aq9lTMgRtAqliE4sjOzS8L+BVHA+Kkc/hA/iVIj4f4/DPJk8EzJ+pVGVQLLtmwgbaO/ACCwXnpEwIDAQAB"; 

    // Generate your own 20 random bytes, and put them here.
    private static final byte[] SALT = new byte[] {
        -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64,
        89
    };

    private TextView mStatusText;
    private Button mCheckLicenseButton;
    private Button mBuyProdButton;
    private Button mBuySubsButton;

    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    // A handler on the UI thread.
    private Handler mHandler;

    private BillingProcessor bp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);

        mStatusText = (TextView) findViewById(R.id.status_text);
        mCheckLicenseButton = (Button) findViewById(R.id.check_license_button);
        mCheckLicenseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                doCheck();
            }
        });
        mBuyProdButton = (Button) findViewById(R.id.buy_prod_button);
        mBuyProdButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                doBuyProduct();
            }
        });
        mBuySubsButton = (Button) findViewById(R.id.buy_subs_button);
        mBuySubsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                doBuySubscription();
            }
        });

        bp = new BillingProcessor(this, BASE64_PUBLIC_KEY, new MainActivity.MyBillingProcessor());

        mHandler = new Handler();

        // Try to use more data here. ANDROID_ID is a single point of attack.
        String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        // Library calls this when it's done.
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        // Construct the LicenseChecker with a policy.
        mChecker = new LicenseChecker(
            this, new APKExpansionPolicy(this,
                new AESObfuscator(SALT, getPackageName(), deviceId)),
            BASE64_PUBLIC_KEY);
        doCheck();
    }

    protected Dialog onCreateDialog(int id) {
        final boolean bRetry = id == 1;
        return new AlertDialog.Builder(this)
            .setTitle(R.string.unlicensed_dialog_title)
            .setMessage(bRetry ? R.string.unlicensed_dialog_retry_body : R.string.unlicensed_dialog_body)
            .setPositiveButton(bRetry ? R.string.retry_button : R.string.buy_button, new DialogInterface.OnClickListener() {
                boolean mRetry = bRetry;
                public void onClick(DialogInterface dialog, int which) {
                    if ( mRetry ) {
                        doCheck();
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "http://market.android.com/details?id=" + getPackageName()));
                            startActivity(marketIntent);                        
                    }
                }
            })
            .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).create();
    }

    private void doCheck() {
        mCheckLicenseButton.setEnabled(false);
        setProgressBarIndeterminateVisibility(true);
        mStatusText.setText(R.string.checking_license);
        mChecker.checkAccess(mLicenseCheckerCallback);
    }

    private void displayResult(final String result) {
        mHandler.post(new Runnable() {
            public void run() {
                mStatusText.setText(result);
                setProgressBarIndeterminateVisibility(false);
                mCheckLicenseButton.setEnabled(true);
            }
        });
        //toHttpBin(result);
        HttpBin.post(result);
    }
    
    private void displayDialog(final boolean showRetry) {
        mHandler.post(new Runnable() {
            public void run() {
                setProgressBarIndeterminateVisibility(false);
                showDialog(showRetry ? 1 : 0);
                mCheckLicenseButton.setEnabled(true);
            }
        });
    }    

    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        public void allow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.
            displayResult(getString(R.string.allow));
        }

        public void dontAllow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            displayResult(getString(R.string.dont_allow));
            // Should not allow access. In most cases, the app should assume
            // the user has access unless it encounters this. If it does,
            // the app should inform the user of their unlicensed ways
            // and then either shut down the app or limit the user to a
            // restricted set of features.
            // In this example, we show a dialog that takes the user to Market.
            // If the reason for the lack of license is that the service is
            // unavailable or there is another problem, we display a
            // retry button on the dialog and a different message.
            displayDialog(policyReason == Policy.RETRY);
        }

        public void applicationError(int errorCode) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // This is a polite way of saying the developer made a mistake
            // while setting up or calling the license checker library.
            // Please examine the error code and fix the error.
            String result = String.format(getString(R.string.application_error), errorCode);
            displayResult(result);
        }
    }

    @Override
    protected void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
        mChecker.onDestroy();
    }

    /*
    private void toHttpBin(String msg) {
        HashMap<String, Object> headers = new HashMap<String, Object>();
        headers.put("accept", "application/json");

        RequestParams params = new RequestParams();
        params.add("msg", msg);

        HttpBin.post("", headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.w(TAG, "--- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // TODO Pull out the first item
                try {
                    // Pull out the first event on the public timeline
                    JSONObject firstItem = (JSONObject) response.get(0);
                    String firstText = firstItem.getString("text");
                    // Do something with the response
                    System.out.println(firstText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    */

    private class MyBillingProcessor implements BillingProcessor.IBillingHandler {
        /*
        NAME/ID                                                     PRICE       TYPE	                LAST UPDATE	STATUS
        Device License 1 (e458316bddd740e2b24140b793c9e42d)         GBP 0.50    Managed product         20 Jan 2016 Active
        Monthly Subscription 1 (ee87fbee14c84df3bb8208af91cced1a)   GBP 0.50    Monthly subscription    20 Jan 2016 Active
        Quarterly Subscription 1 (48381ca0dbe8424288a29ad7c8fd345d) GBP 1.00    3 month subscription    20 Jan 2016 Active
        Weekly Subscription 1 (50b102c4bbb94cf39a3f491816749ce2)    GBP 0.50    Weekly subscription     20 Jan 2016 Inactive
         */
        // IBillingHandler implementation

        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        @Override
        public void onBillingInitialized() {
            ArrayList<String> productIdList = new ArrayList<>();
            productIdList.add("e458316bddd740e2b24140b793c9e42d");
            List<SkuDetails> skuProdList = bp.getPurchaseListingDetails(productIdList);
            for (SkuDetails sku : skuProdList) {
                Log.w(TAG, "Product: " + sku.description);
                mStatusText.append("Product: " + sku.description);
            }

            productIdList.clear();
            productIdList.add("ee87fbee14c84df3bb8208af91cced1a");
            productIdList.add("48381ca0dbe8424288a29ad7c8fd345d");
            productIdList.add("50b102c4bbb94cf39a3f491816749ce2");
            List<SkuDetails> skuSubsList = bp.getSubscriptionListingDetails(productIdList);
            for (SkuDetails sku : skuSubsList) {
                Log.w(TAG, "Subscription: " + sku.description);
                mStatusText.append("Subscription: " + sku.description);
            }

            bp.loadOwnedPurchasesFromGoogle();
        }

        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        @Override
        public void onProductPurchased(String productId, TransactionDetails details) {
            // TODO handle product purchase
            Log.w(TAG, String.format("Product purchased: %s. Transaction details: %s", productId, details.purchaseInfo.responseData));
            mBuyProdButton.setEnabled(true);
            mBuySubsButton.setEnabled(true);
            mStatusText.setText("");
        }

        /*
         * Called when some error occurred. See Constants class for more details
         */
        @Override
        public void onBillingError(int errorCode, Throwable error) {
            // TODO handle billing error
            Log.w(TAG, String.format("Billing error: %d", errorCode));
            if (error != null)
                error.printStackTrace();
            mBuyProdButton.setEnabled(true);
            mBuySubsButton.setEnabled(true);
            mStatusText.setText("");
        }

        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
        @Override
        public void onPurchaseHistoryRestored() {
            // TODO handle purchase history restored
            Log.w(TAG, "Purchase history restored!!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void doBuyProduct() {
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if(!isAvailable) {
            // Don't buy
            Log.w(TAG, "IAB not available");
        }
        mBuyProdButton.setEnabled(false);
        setProgressBarIndeterminateVisibility(true);
        mStatusText.setText(R.string.buying_product);
        bp.purchase(this, "e458316bddd740e2b24140b793c9e42d");
    }

    private void doBuySubscription() {
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if(!isAvailable) {
            // Don't buy
            Log.w(TAG, "IAB not available");
        }
        mBuySubsButton.setEnabled(false);
        setProgressBarIndeterminateVisibility(true);
        mStatusText.setText(R.string.buying_subscription);
        bp.subscribe(this, "ee87fbee14c84df3bb8208af91cced1a");
    }

}
