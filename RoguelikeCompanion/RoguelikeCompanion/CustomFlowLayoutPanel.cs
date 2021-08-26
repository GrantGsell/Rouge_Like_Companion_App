using System.Windows.Forms;

namespace RoguelikeCompanion
{   
    /*
     * Creates a custom flow layout panel to reduce/remove screen flickering.
     */
    class CustomFlowLayoutPanel : FlowLayoutPanel
    {
        public CustomFlowLayoutPanel()
        {
            this.DoubleBuffered = true;
        }
        protected override void OnScroll(ScrollEventArgs se)
        {
            this.Invalidate();
            base.OnScroll(se);
        }
    }
}
