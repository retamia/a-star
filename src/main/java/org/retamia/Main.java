package org.retamia;

import javax.swing.*;
import java.util.*;

public class Main {

    // 地图元素
    static final char START   = 'S';  // 起点
    static final char END     = 'E';  // 终点
    static final char SPACE   = '.';  // 空地
    static final char WALL    = 'W';  // 墙
    static final char VISITED = '-';  // 探索过
    static final char ON_PATH = '@';  // 前进路径

    // 地图字符串
    static final String[] S_MAP = {
            ". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .",
            ". W W W W W W . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .",
            ". . . . . . W . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .",
            ". . . . . . W . . . . . . . . E . . . . . . . . . . . . . . . . . . . . . . . .",
            ". . . . . . W . . . . . W W W W W W W W W W W W . . . . . . . . . . . . . . . .",
            ". . . . . . W . . . . W W . . . . W W . . . . . . . . . . . . . . . . . . . . .",
            ". . . . . . W . . . . W W . . . . . W W . . . . . . . . . . . . . . . . . . . .",
            ". . . . . . W . . W W W W . . . . . W W . . . . . . . . . . . . . . . . . . . .",
            ". W W W W W W . . W W . . . . . . . . W W . . . . . . . . . . . . . . . . . . .",
            ". . . . . . W . . W W . . . . . . . . W W . . . . . . . . . . . . . . W W . W W",
            ". . . . . . W . W W W . . . . . . . . W W . . . . . . . . . . . . . W W . . . .",
            ". . . . . . W . . . . . . . . . . . . . . . . . . . . . . . . . . . W . . . . .",
            ". . . . . . W . . . . . . . . . . . . . . . . . . . . . . . . . . . W W . . . .",
            ". . . . . . W . . . . . . . . S . . . . . . . . . . . . . . . . . . W W . W W W",
            ". . . . . . W . . . . . . . . . . . . . . . . . . . . . . . . . . . . W . . . .",
            ". . . . . . W . . . . . . . . . . . . . . . . . . . . . . . . . . . . W . . . .",
            ". . . . . . W . . . . . . . . . . . . . . . . . . . . . . . . . . . . W . . . .",
            ". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . W. . . .",

    };

    // 地图
    static char[][] MAP = new char[S_MAP[0].replace(" ", "").length()][S_MAP.length];

    static Geometry.Point MAX_POINT = new Geometry.Point(MAP.length, MAP[0].length);

    static Geometry.Point START_POINT = null;
    static Geometry.Point END_POINT = null;

    static Set<PathFinderData> closedTable = new HashSet<>();
    static PriorityQueue<PathFinderData> openedTable = new PriorityQueue<>();

    static Geometry.Vector leftDirect = new Geometry.Vector(-1, 0);
    static Geometry.Vector rightDirect = new Geometry.Vector(1, 0);
    static Geometry.Vector upDirect = new Geometry.Vector(0, -1);
    static Geometry.Vector downDirect = new Geometry.Vector(0, 1);

    public static void main(String[] args) {
        genMap();
        printMap();

        long start_time = System.currentTimeMillis();
        findPath();
        System.out.println(System.currentTimeMillis() - start_time);

        printMap();
    }

    static void findPath()
    {
        openedTable.clear();
        closedTable.clear();

        PathFinderData startData = new PathFinderData(START_POINT, 0, 0, null);
        openedTable.add(startData);
        ArrayList<Geometry.Vector> direction = new ArrayList<>(4);
        direction.add(leftDirect);
        direction.add(rightDirect);
        direction.add(upDirect);
        direction.add(downDirect);

        PathFinderData endData = null;

        while (!openedTable.isEmpty()) {
            PathFinderData currentPathFinderData = openedTable.remove();

            if (MAP[currentPathFinderData.point.x][currentPathFinderData.point.y] == SPACE) // 将取出的点标识为已访问点
            {
                MAP[currentPathFinderData.point.x][currentPathFinderData.point.y] = VISITED;
            }

            for (Geometry.Vector direct: direction) {

                Geometry.Point target = new Geometry.Point(currentPathFinderData.point.x + direct.x, currentPathFinderData.point.y + direct.y);
                if (checkEdge(target)) {
                    continue;
                }

                char flag = MAP[target.x][target.y];

                PathFinderData newData = new PathFinderData(
                        target,
                        currentPathFinderData.cost + moveCost(direct),
                        hManhattanDistance(target, END_POINT),
                        currentPathFinderData);

                if (closedTable.contains(newData)) {
                    continue;
                }

                if (flag == END) {
                    endData = newData;
                    break;
                }

                if (flag == WALL) {
                    continue;
                }

                PathFinderData existedData = null;

                for (PathFinderData data: openedTable) {
                    if (data.equals(newData)) {
                        existedData = data;
                        break;
                    }
                }

                if (existedData != null) {
                    if (existedData.cost > newData.cost) {
                        existedData.cost = newData.cost;
                        existedData.parent = currentPathFinderData;
                    }
                } else {
                    openedTable.add(newData);
                }

                closedTable.add(currentPathFinderData);
            }

            if (endData != null) {
                break;
            }
        }

        if (endData == null) {
            System.err.println("找不到路径");
        }

        // 反向找出路径
        for (PathFinderData pathData = endData; pathData != null; )
        {
            Geometry.Point pnt = pathData.point;
            if (MAP[pnt.x][pnt.y] == VISITED)
            {
                MAP[pnt.x][pnt.y] = ON_PATH;
            }
            pathData = pathData.parent;
        }
    }

    static double moveCost(Geometry.Vector direct) {
        return 1;
    }

    static boolean checkEdge(Geometry.Point pos) {
        return (pos.x < 0 || pos.y < 0) || (pos.x >= MAX_POINT.x || pos.y >= MAX_POINT.y);
    }

    static void genMap() {
        int row = 0;

        for (String s: S_MAP) {
            char[] c_s = s.replace(" ", "").toCharArray();
            for (int col = 0; col < c_s.length; col++) {
                MAP[col][row] = c_s[col];
                switch (c_s[col]) {
                    case START:
                        START_POINT = new Geometry.Point(col, row);
                        break;
                    case END:
                        END_POINT = new Geometry.Point(col, row);
                        break;
                }
            }

            row++;
        }
    }

    static void printMap()
    {
        for (int j = 0; j < MAX_POINT.y; j++)
        {
            for (int i = 0; i < MAX_POINT.x; i++)
            {
                System.out.printf("%c ", MAP[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * 曼哈顿距离,小于等于实际值
     */
    static double hManhattanDistance(Geometry.Point current, Geometry.Point target)
    {
        return 5 * Math.abs(current.x - target.x) + Math.abs(current.y - target.y);
    }

    static double hBFS(Geometry.Point current, Geometry.Point target)
    {
        return 0;
    }
}
