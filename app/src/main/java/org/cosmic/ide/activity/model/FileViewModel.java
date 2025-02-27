package org.cosmic.ide.activity.model;

import android.os.Environment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.cosmic.ide.ui.treeview.TreeNode;
import org.cosmic.ide.ui.treeview.TreeUtil;
import org.cosmic.ide.ui.treeview.file.TreeFile;
import org.cosmic.ide.common.util.CoroutineUtil;

import java.io.File;

public class FileViewModel extends ViewModel {

    private MutableLiveData<File> mRoot =
            new MutableLiveData<>(Environment.getExternalStorageDirectory());
    private MutableLiveData<TreeNode<TreeFile>> mNode = new MutableLiveData<>();

    public LiveData<TreeNode<TreeFile>> getNodes() {
        return mNode;
    }

    public LiveData<File> getRootFile() {
        return mRoot;
    }

    public void setRootFile(File root) {
        mRoot.setValue(root);
        refreshNode(root);
    }

    public void refreshNode(File root) {
        CoroutineUtil.execute(() -> {
            TreeNode<TreeFile> node = TreeNode.root(TreeUtil.getNodes(root));
            mNode.postValue(node);
        });
    }
}
