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
                    if (isViewingShorts(rootNode)) {
                        // Instantly steps back out of the Shorts player
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }
                    rootNode.recycle();
                }
            }
        }
    }

    private boolean isViewingShorts(AccessibilityNodeInfo node) {
        if (node == null) return false;

        // Check view IDs or content descriptions commonly used in YouTube Shorts
        CharSequence viewId = node.getViewIdResourceName();
        CharSequence contentDesc = node.getContentDescription();

        if (viewId != null && (viewId.toString().contains("reel_player") || 
                               viewId.toString().contains("shorts") ||
                               viewId.toString().contains("reel_watch"))) {
            return true;
        }

        if (contentDesc != null && contentDesc.toString().toLowerCase().contains("short")) {
            return true;
        }

        // Recursively check child elements
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
