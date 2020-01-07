/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.infra.docattrs;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.attrs.Attr;
import org.carrot2.attrs.AttrBoolean;
import org.carrot2.attrs.AttrDouble;
import org.carrot2.attrs.AttrEnum;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.attrs.AttrObject;
import org.carrot2.attrs.AttrObjectArray;
import org.carrot2.attrs.AttrString;
import org.carrot2.attrs.AttrStringArray;
import org.carrot2.attrs.AttrVisitor;
import org.carrot2.attrs.ClassNameMapper;
import org.carrot2.attrs.Constraint;

class AttrInfoCollector implements AttrVisitor {
  private final ClassNameMapper aliasMapper;
  private final Map<String, AttrInfo> attrs;

  public AttrInfoCollector(Map<String, AttrInfo> attributes, ClassNameMapper aliasMapper) {
    this.attrs = attributes;
    this.aliasMapper = aliasMapper;
  }

  @Override
  public void visit(String key, AttrBoolean attr) {
    attrInfo(key, attr, () -> "Boolean");
  }

  @Override
  public void visit(String key, AttrInteger attr) {
    attrInfo(key, attr, () -> "Integer");
  }

  @Override
  public void visit(String key, AttrDouble attr) {
    attrInfo(key, attr, () -> "Double");
  }

  @Override
  public void visit(String key, AttrString attr) {
    attrInfo(key, attr, () -> "String");
  }

  @Override
  public <T extends Enum<T>> void visit(String key, AttrEnum<T> attr) {
    attrInfo(key, attr, () -> attr.enumClass().getName());
  }

  @Override
  public void visit(String key, AttrStringArray attr) {
    attrInfo(key, attr, () -> "String[]");
  }

  @Override
  public <T extends AcceptingVisitor> void visit(String key, AttrObject<T> attr) {
    attrInfo(key, attr, () -> attr.getInterfaceClass().getName());
  }

  @Override
  public <T extends AcceptingVisitor> void visit(String key, AttrObjectArray<T> attr) {
    attrInfo(key, attr, () -> attr.getInterfaceClass().getName() + "[]");
    if (attr.get() != null && !attr.get().isEmpty()) {
      throw new RuntimeException(
          "Don't know how to emit value for non-empty array attribute: " + key);
    }
  }

  private <T> AttrInfo attrInfo(String key, Attr<T> attr, Supplier<String> type) {
    AttrInfo info = new AttrInfo();
    info.attr = attr;
    info.description = attr.getDescription();
    info.type = type.get();

    List<Constraint<? super T>> constraints = attr.getConstraints();
    if (!constraints.isEmpty()) {
      info.constraints =
          constraints.stream().map(Constraint::description).collect(Collectors.toList());
    }

    Optional<Object> value = Optional.ofNullable(attr.get());
    if (attr instanceof AttrObject<?>) {
      value = value.map(aliasMapper::toName);
    } else {
      value = value.map(Objects::toString);
    }
    info.value = value.orElse(null);

    attrs.put(key, info);
    return info;
  }
}
