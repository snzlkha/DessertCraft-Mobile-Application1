package com.yourapp.desertcraft;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yourapp.desertcraft.databinding.ActivityContainerBinding;
import com.yourapp.desertcraft.fragments.CartFragment;
import com.yourapp.desertcraft.fragments.DesignFragment;
import com.yourapp.desertcraft.fragments.HomeFragment;
import com.yourapp.desertcraft.fragments.MenuFragment;
import com.yourapp.desertcraft.fragments.OrderFragment;
import com.yourapp.desertcraft.fragments.PreviewFragment;
import com.yourapp.desertcraft.fragments.ProfileFragment;
import com.yourapp.desertcraft.fragments.TrackingFragment;
import com.yourapp.desertcraft.utils.ReplaceFragmentListener;

public class ContainerActivity extends AppCompatActivity implements ReplaceFragmentListener {
    private ActivityContainerBinding binding;
    private boolean isBackNavigation = false;
    private int lastSelectedItemId;

    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = binding.bottomNavigationView;

        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        getSupportFragmentManager().addOnBackStackChangedListener(this::onBackStackChanged);

        replaceFragment(new HomeFragment(), false);

        setBackPressListener();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == lastSelectedItemId) {
            return true;
        }

        Fragment selectedFragment = getSelectedFragment(item.getItemId());

        if (selectedFragment != null) {
            replaceFragment(selectedFragment, true);
            lastSelectedItemId = item.getItemId();
            isBackNavigation = false;
        }

        return true;
    }

    private Fragment getSelectedFragment(int itemId) {
        if (itemId == R.id.navigation_home) {
            return new HomeFragment();
        } else if (itemId == R.id.navigation_menu) {
            return new MenuFragment();
        } else if (itemId == R.id.navigation_design) {
            return new DesignFragment();
        } else if (itemId == R.id.navigation_account) {
            return new ProfileFragment();
        } else return null;
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (isBackNavigation) {
            transaction.setCustomAnimations(0, R.anim.fade_out_500);
        } else {
            transaction.setCustomAnimations(0, R.anim.stretch_out);
        }

        transaction.replace(R.id.fragmentContainer, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void setBackPressListener() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else if (binding.bottomNavigationView.getSelectedItemId() != R.id.navigation_home) {
                    isBackNavigation = true;
                    binding.bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                } else {
                    finish();
                }
            }
        });
    }

    private void onBackStackChanged() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof HomeFragment) {
            lastSelectedItemId = R.id.navigation_home;
        } else if (currentFragment instanceof MenuFragment) {
            lastSelectedItemId = R.id.navigation_menu;
        } else if (currentFragment instanceof DesignFragment) {
            lastSelectedItemId = R.id.navigation_design;
        } else if (currentFragment instanceof ProfileFragment) {
            lastSelectedItemId = R.id.navigation_account;
        } else if (currentFragment instanceof OrderFragment) {
            return;
        }

        bottomNavigationView.setSelectedItemId(lastSelectedItemId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onBackStackChanged();
    }

    @Override
    public void onOrderNowClick() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_menu);
    }

    @Override
    public void onTrackNowClick() {
        replaceFragment(new OrderFragment(), true);
    }

    @Override
    public void openCartFragment() {
        replaceFragment(new CartFragment(), true);
    }

    @Override
    public void onCartItemClick(int itemId, int points) {
        Bundle bundle = new Bundle();
        bundle.putInt("selectedItemId", itemId);
        bundle.putInt("availablePoints", points);
        PreviewFragment previewFragment = new PreviewFragment();
        previewFragment.setArguments(bundle);
        replaceFragment(previewFragment, true);
    }

    @Override
    public void onEditItemClick(int itemId, String did) {
        Bundle bundle = new Bundle();
        bundle.putInt("itemId", itemId);
        bundle.putString("productId", did);
        MenuFragment editItemFragment = new MenuFragment();
        editItemFragment.setArguments(bundle);
        replaceFragment(editItemFragment, true);
    }

    @Override
    public void openTrackingFragment(int orderId) {
        Bundle bundle = new Bundle();
        bundle.putInt("orderId", orderId);
        TrackingFragment trackingFragment = new TrackingFragment();
        trackingFragment.setArguments(bundle);
        replaceFragment(trackingFragment, true);
    }

    @Override
    public void goBackToHome() {
        replaceFragment(new HomeFragment(), false);
    }

}