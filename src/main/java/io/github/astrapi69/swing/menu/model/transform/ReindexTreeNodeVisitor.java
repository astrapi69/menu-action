/**
 * The MIT License
 *
 * Copyright (C) 2021 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
