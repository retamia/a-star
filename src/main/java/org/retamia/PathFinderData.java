package org.retamia;

import java.util.Objects;

public class PathFinderData implements Comparable<PathFinderData> {
    Geometry.Point point;
    double cost;
    double heuristicValue;
    PathFinderData parent;

    public PathFinderData(Geometry.Point point, double cost, double heuristicValue, PathFinderData parent)
    {
        this.point = point;
        this.cost = cost;
        this.heuristicValue = heuristicValue;
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathFinderData)) return false;
        PathFinderData that = (PathFinderData) o;
        return Objects.equals(point, that.point);
    }

    double getMaybeDistance()
    {
        return cost + heuristicValue;
    }

    @Override
    public int hashCode() {
        return String.format("%d,%d", this.point.x, this.point.y).hashCode();
    }

    @Override
    public int compareTo(PathFinderData other) {
        if (this.getMaybeDistance() > other.getMaybeDistance()) {
            return 1;
        } else {
            return -1;
        }
    }
}
