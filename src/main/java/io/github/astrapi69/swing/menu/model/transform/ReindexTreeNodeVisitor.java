package io.github.astrapi69.swing.menu.model.transform;

import java.util.Collection;

import io.github.astrapi69.data.identifiable.IdGenerator;
import io.github.astrapi69.design.pattern.visitor.Visitor;
import io.github.astrapi69.gen.tree.api.IBaseTreeNode;
import io.github.astrapi69.gen.tree.api.ITreeNode;
import lombok.Getter;
import lombok.NonNull;

/**
 * This visitor visits all {@link ITreeNode} objects and adds them to a {@link Collection} object
 * with all descendant
 *
 * @param <T>
 *            the generic type of the value
 */
public class ReindexTreeNodeVisitor<V, K, T extends IBaseTreeNode<V, K, T>> implements Visitor<T>
{
	/**
	 * The {@link IdGenerator} object for reindex tree ids
	 */
	@Getter
	private final IdGenerator<K> idGenerator;

	public ReindexTreeNodeVisitor(final @NonNull IdGenerator<K> idGenerator)
	{
		this.idGenerator = idGenerator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(T treeNode)
	{
		treeNode.setId(idGenerator.getNextId());
	}
}
