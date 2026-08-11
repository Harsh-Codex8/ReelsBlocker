package com.example.reelblocker;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class ReelBlockerService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence packageName = event.getPackageName();
        if (packageName == null) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        String pkg = packageName.toString();

        if (pkg.equals("com.instagram.android") || pkg.equals("com.google.android.youtube")) {
            if (isElementPresent(rootNode, "Reels") || isElementPresent(rootNode, "Shorts")) {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        }
        rootNode.recycle();
    }

    private boolean isElementPresent(AccessibilityNodeInfo node, String textToFind) {
        if (node == null) return false;
        
        CharSequence text = node.getText();
        if (text != null && text.toString().toLowerCase().contains(textToFind.toLowerCase())) {
            return true;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (isElementPresent(child, textToFind)) {
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
