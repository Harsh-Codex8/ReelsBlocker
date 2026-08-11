package com.example.reelblocker;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class ReelBlockerService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() != null) {
            String packageName = event.getPackageName().toString();

            // Check if user is using YouTube
            if (packageName.equals("com.google.android.youtube")) {
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                if (rootNode != null) {
                    // Search screen nodes to see if it's currently playing a Short
                    if (isViewingShorts(rootNode)) {
                        // Instantly steps back out of the Shorts player to your current page
                        performGlobalAction(GLOBAL_ACTION_BACK);
                        Toast.makeText(this, "Shorts Blocked!", Toast.LENGTH_SHORT).show();
                    }
                    rootNode.recycle();
                }
            }
        }
    }

    private boolean isViewingShorts(AccessibilityNodeInfo node) {
        // Look for layout elements unique to the YouTube Shorts player
        if (node.getViewIdResourceName() != null && 
            node.getViewIdResourceName().contains("reel_player")) {
            return true;
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (isViewingShorts(child)) {
                    child.recycle();
                    return true;
                }
                child.recycle();
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {
        // Required override
    }
}
