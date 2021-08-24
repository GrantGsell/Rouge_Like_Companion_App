
namespace RoguelikeCompanion
{
    partial class Shrines
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
            this.shrineTextBox = new System.Windows.Forms.TextBox();
            this.btnDiceEffects = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // shrineTextBox
            // 
            this.shrineTextBox.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(128)))), ((int)(((byte)(0)))));
            this.shrineTextBox.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.shrineTextBox.Dock = System.Windows.Forms.DockStyle.Top;
            this.shrineTextBox.Font = new System.Drawing.Font("Modern No. 20", 18F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point);
            this.shrineTextBox.Location = new System.Drawing.Point(0, 0);
            this.shrineTextBox.Name = "shrineTextBox";
            this.shrineTextBox.Size = new System.Drawing.Size(1393, 33);
            this.shrineTextBox.TabIndex = 0;
            this.shrineTextBox.TabStop = false;
            this.shrineTextBox.Text = "Hover Over Shrine Image to See Effects";
            this.shrineTextBox.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            // 
            // btnDiceEffects
            // 
            this.btnDiceEffects.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(128)))), ((int)(((byte)(0)))));
            this.btnDiceEffects.FlatAppearance.BorderSize = 0;
            this.btnDiceEffects.FlatAppearance.MouseOverBackColor = System.Drawing.Color.DeepSkyBlue;
            this.btnDiceEffects.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnDiceEffects.Font = new System.Drawing.Font("Modern No. 20", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point);
            this.btnDiceEffects.Location = new System.Drawing.Point(1130, 0);
            this.btnDiceEffects.Margin = new System.Windows.Forms.Padding(0);
            this.btnDiceEffects.Name = "btnDiceEffects";
            this.btnDiceEffects.Size = new System.Drawing.Size(251, 33);
            this.btnDiceEffects.TabIndex = 1;
            this.btnDiceEffects.Text = "Dice Effects By Name";
            this.btnDiceEffects.UseVisualStyleBackColor = false;
            this.btnDiceEffects.Click += new System.EventHandler(this.btnDiceEffects_Click);
            // 
            // Shrines
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1393, 900);
            this.Controls.Add(this.btnDiceEffects);
            this.Controls.Add(this.shrineTextBox);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "Shrines";
            this.Text = "Form1";
            this.Load += new System.EventHandler(this.Shrines_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox shrineTextBox;
        private System.Windows.Forms.Button btnDiceEffects;
    }
}