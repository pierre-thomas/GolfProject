import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.mapeditor.core.Map;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.core.Tile;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.TileLayer;
import org.mapeditor.io.TMXMapReader;
import org.mapeditor.view.HexagonalRenderer;
import org.mapeditor.view.MapRenderer;
import org.mapeditor.view.OrthogonalRenderer;
import org.mapeditor.view.IsometricRenderer;

/**
 * An example showing how to use libtiled-java to do a simple TMX viewer.
 */
public class TMXViewer
{
    public static void main(String[] arguments) {
        String fileToOpen = "Trou1.tmx";

        Map map;
        try {
            TMXMapReader mapReader = new TMXMapReader();
            map = mapReader.readMap(fileToOpen);
        } catch (Exception e) {
            System.out.println("Error while reading the map:\n" + e.getMessage());
            return;
        }

        System.out.println(map.toString() + " loaded");
        
        // Info recuperables de la map
        System.out.println("map.getWidth() = " + map.getWidth());
        System.out.println("map.getHeight() = " + map.getHeight());
        System.out.println("map.getTileWidth() = " + map.getTileWidth());
        System.out.println("map.getTileHeight() = " + map.getTileHeight());
        System.out.println("map.getLayerCount() = " + map.getLayerCount());
        
        // Info recuperables d'une tile trouvee dans une layer
        TileLayer layer0 = (TileLayer) map.getLayer(0);
        Tile tile = layer0.getTileAt(0, 0);
        System.out.println("id du tile en (0,0) de la layer 0 = " + tile.getId());
        System.out.println("height du tile en (0,0) de la layer 0 = " + tile.getHeight());
        System.out.println("width du tile en (0,0) de la layer 0 = " + tile.getWidth());
        System.out.println("probability du tile en (0,0) de la layer 0 = " + tile.getProbability());
        System.out.println("source du tile en (0,0) de la layer 0 = " + tile.getSource());
        System.out.println("terrain du tile en (0,0) de la layer 0 = " + tile.getTerrain());
        System.out.println("type du tile en (0,0) de la layer 0 = " + tile.getType());

        // Affichage de la map
        JScrollPane scrollPane = new JScrollPane(new MapView(map));
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        JFrame appFrame = new JFrame("TMX Viewer");
        appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        appFrame.setContentPane(scrollPane);
        appFrame.pack();
        appFrame.setVisible(true);
    }

}

class MapView extends JPanel implements Scrollable
{
    private final Map map;
    private final MapRenderer renderer;

    public MapView(Map map) {
        this.map = map;
        renderer = createRenderer(map);

        setPreferredSize(renderer.getMapSize());
        setOpaque(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g.create();
        final Rectangle clip = g2d.getClipBounds();

        // Draw a gray background
        g2d.setPaint(new Color(100, 100, 100));
        g2d.fill(clip);

        // Draw each map layer
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TileLayer) {
                renderer.paintTileLayer(g2d, (TileLayer) layer);
            } else if (layer instanceof ObjectGroup) {
                renderer.paintObjectGroup(g2d, (ObjectGroup) layer);
            }
        }
    }

    private static MapRenderer createRenderer(Map map) {
        switch (map.getOrientation()) {
            case ORTHOGONAL:
                return new OrthogonalRenderer(map);

            case ISOMETRIC:
                return new IsometricRenderer(map);

            case HEXAGONAL:
                return new HexagonalRenderer(map);

            default:
                return null;
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL)
            return map.getTileWidth();
        else
            return map.getTileHeight();
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            final int tileWidth = map.getTileWidth();
            return (visibleRect.width / tileWidth - 1) * tileWidth;
        } else {
            final int tileHeight = map.getTileHeight();
            return (visibleRect.height / tileHeight - 1) * tileHeight;
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}