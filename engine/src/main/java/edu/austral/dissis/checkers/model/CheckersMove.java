package edu.austral.dissis.checkers.model;

import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;

public record CheckersMove(Position from, Position to) implements Move {}
