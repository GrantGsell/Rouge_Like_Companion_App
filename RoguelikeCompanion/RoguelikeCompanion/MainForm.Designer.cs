
namespace RoguelikeCompanion
{
    partial class MainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.titleBar = new System.Windows.Forms.Panel();
            this.btnMuncherRecipes = new System.Windows.Forms.Button();
            this.newRunButton = new System.Windows.Forms.Button();
            this.closeButton = new System.Windows.Forms.Button();
            this.bunifuDragControl1 = new Bunifu.Framework.UI.BunifuDragControl(this.components);
            this.button1 = new System.Windows.Forms.Button();
            this.titleBar.SuspendLayout();
            this.SuspendLayout();
            // 
            // titleBar
            // 
            this.titleBar.BackColor = System.Drawing.Color.DeepSkyBlue;
            this.titleBar.Controls.Add(this.button1);
            this.titleBar.Controls.Add(this.btnMuncherRecipes);
            this.titleBar.Controls.Add(this.newRunButton);
            this.titleBar.Controls.Add(this.closeButton);
            this.titleBar.Dock = System.Windows.Forms.DockStyle.Top;
            this.titleBar.Location = new System.Drawing.Point(0, 0);
            this.titleBar.Margin = new System.Windows.Forms.Padding(0);
            this.titleBar.Name = "titleBar";
            this.titleBar.Size = new System.Drawing.Size(1808, 40);
            this.titleBar.TabIndex = 0;
            // 
            // btnMuncherRecipes
            // 
            this.btnMuncherRecipes.FlatAppearance.BorderColor = System.Drawing.Color.RoyalBlue;
            this.btnMuncherRecipes.FlatAppearance.BorderSize = 0;
            this.btnMuncherRecipes.FlatAppearance.MouseOverBackColor = System.Drawing.Color.Gold;
            this.btnMuncherRecipes.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnMuncherRecipes.Font = new System.Drawing.Font("Modern No. 20", 16.2F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point);
            this.btnMuncherRecipes.Location = new System.Drawing.Point(500, 0);
            this.btnMuncherRecipes.Margin = new System.Windows.Forms.Padding(0);
            this.btnMuncherRecipes.Name = "btnMuncherRecipes";
            this.btnMuncherRecipes.Size = new System.Drawing.Size(250, 40);
            this.btnMuncherRecipes.TabIndex = 1;
            this.btnMuncherRecipes.Text = "Muncher Recipes";
            this.btnMuncherRecipes.UseVisualStyleBackColor = true;
            this.btnMuncherRecipes.Click += new System.EventHandler(this.btnMuncherRecipes_Click);
            // 
            // newRunButton
            // 
            this.newRunButton.Dock = System.Windows.Forms.DockStyle.Left;
            this.newRunButton.FlatAppearance.BorderSize = 0;
            this.newRunButton.FlatAppearance.MouseOverBackColor = System.Drawing.Color.Lime;
            this.newRunButton.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.newRunButton.Font = new System.Drawing.Font("Modern No. 20", 16.2F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point);
            this.newRunButton.Location = new System.Drawing.Point(0, 0);
            this.newRunButton.Margin = new System.Windows.Forms.Padding(0);
            this.newRunButton.Name = "newRunButton";
            this.newRunButton.Size = new System.Drawing.Size(500, 40);
            this.newRunButton.TabIndex = 1;
            this.newRunButton.Text = "New Run";
            this.newRunButton.UseVisualStyleBackColor = true;
            this.newRunButton.Click += new System.EventHandler(this.newRunButton_Click);
            // 
            // closeButton
            // 
            this.closeButton.Dock = System.Windows.Forms.DockStyle.Right;
            this.closeButton.FlatAppearance.BorderSize = 0;
            this.closeButton.FlatAppearance.MouseOverBackColor = System.Drawing.Color.Red;
            this.closeButton.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.closeButton.Font = new System.Drawing.Font("Modern No. 20", 16.2F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point);
            this.closeButton.Location = new System.Drawing.Point(1728, 0);
            this.closeButton.Margin = new System.Windows.Forms.Padding(0);
            this.closeButton.Name = "closeButton";
            this.closeButton.Size = new System.Drawing.Size(80, 40);
            this.closeButton.TabIndex = 1;
            this.closeButton.Text = "X";
            this.closeButton.UseVisualStyleBackColor = true;
            this.closeButton.Click += new System.EventHandler(this.closeButton_Click);
            // 
            // bunifuDragControl1
            // 
            this.bunifuDragControl1.Fixed = true;
            this.bunifuDragControl1.Horizontal = true;
            this.bunifuDragControl1.TargetControl = this.titleBar;
            this.bunifuDragControl1.Vertical = true;
            // 
            // button1
            // 
            this.button1.BackgroundImageLayout = System.Windows.Forms.ImageLayout.None;
            this.button1.FlatAppearance.BorderSize = 0;
            this.button1.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(128)))), ((int)(((byte)(0)))));
            this.button1.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.button1.Font = new System.Drawing.Font("Modern No. 20", 16.2F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point);
            this.button1.Location = new System.Drawing.Point(750, 0);
            this.button1.Margin = new System.Windows.Forms.Padding(0);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(150, 40);
            this.button1.TabIndex = 1;
            this.button1.Text = "Shrines";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackgroundImageLayout = System.Windows.Forms.ImageLayout.None;
            this.ClientSize = new System.Drawing.Size(1808, 939);
            this.Controls.Add(this.titleBar);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "MainForm";
            this.Text = "Form1";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.LocationChanged += new System.EventHandler(this.Form1_LocationChanged);
            this.titleBar.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel titleBar;
        private Bunifu.Framework.UI.BunifuDragControl bunifuDragControl1;
        private System.Windows.Forms.Button closeButton;
        private System.Windows.Forms.Button newRunButton;
        private System.Windows.Forms.Button btnMuncherRecipes;
        private System.Windows.Forms.Button button1;
    }
}