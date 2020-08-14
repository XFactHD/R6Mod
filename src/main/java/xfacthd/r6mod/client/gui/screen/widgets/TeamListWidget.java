package xfacthd.r6mod.client.gui.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;

import java.util.Collection;

public class TeamListWidget extends ExtendedList<TeamListWidget.TeamEntry>
{
    public static final String NO_TEAM = I18n.format("gui.r6mod.camera.no_team");

    private final FontRenderer font;
    private final int listWidth;

    public TeamListWidget(int x, int y, int width, int height, Collection<String> teams, FontRenderer font)
    {
        super(Minecraft.getInstance(), width, height, y, y + height, font.FONT_HEIGHT * 2);
        this.font = font;
        this.listWidth = width;

        this.x0 = x;
        this.x1 = x + width;

        addEntry(new TeamEntry("null", this));
        for (String team : teams) { addEntry(new TeamEntry(team, this)); }
        children().sort((t1, t2) ->
        {
            if (t1.team.equals("null")) { return -1; }
            if (t2.team.equals("null")) { return 1; }
            return t1.team.compareTo(t2.team);
        });
    }

    @Override
    protected void renderHoleBackground(int top, int bottom, int alphaTop, int alphaBottom) { /*NOOP*/ }

    @Override
    protected int getScrollbarPosition() { return x0 + this.listWidth - 6; }

    @Override
    public int getRowWidth() { return this.listWidth - 18; }

    public static class TeamEntry extends ExtendedList.AbstractListEntry<TeamEntry>
    {
        private final String team;
        private final TeamListWidget parent;

        public TeamEntry(String team, TeamListWidget parent)
        {
            this.team = team;
            this.parent = parent;
        }

        @Override
        public void render(int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean mouseOver, float partialTicks)
        {
            String text = team.equals("null") ? NO_TEAM : team;
            parent.font.drawStringWithShadow(text, left, top, 0xFFFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            parent.setSelected(this);
            return false;
        }

        public String getTeam() { return team; }
    }
}