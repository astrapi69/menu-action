package io.github.astrapi69.swing.menu.model.transform;

import io.github.astrapi69.gen.tree.BaseTreeNode;
import io.github.astrapi69.swing.menu.model.MenuInfo;

import java.util.Comparator;

public class BaseTreeNodeByValueComparator implements Comparator<BaseTreeNode<MenuInfo, Long>> {
	@Override public int compare(BaseTreeNode<MenuInfo, Long> o1, BaseTreeNode<MenuInfo, Long> o2) {
		return Integer.compare(o1.getValue().getOrdinal(), o2.getValue().getOrdinal());
	}
}
