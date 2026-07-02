package edu.austral.dissis.common.model;

public enum Color {
  WHITE,
  BLACK;

  public Color opposite() {
    return this == WHITE ? BLACK : WHITE;
  }
}
