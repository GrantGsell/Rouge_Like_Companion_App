
namespace RoguelikeCompanion
{
    partial class Form1
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
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
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.pictureBoxFullImage = new System.Windows.Forms.PictureBox();
            this.capture = new System.Windows.Forms.Button();
            this.pictureBoxCrop = new System.Windows.Forms.PictureBox();
            this.pictureBoxBorders = new System.Windows.Forms.PictureBox();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxFullImage)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxCrop)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxBorders)).BeginInit();
            this.SuspendLayout();
            // 
            // pictureBoxFullImage
            // 
            this.pictureBoxFullImage.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.pictureBoxFullImage.Location = new System.Drawing.Point(12, 12);
            this.pictureBoxFullImage.Name = "pictureBoxFullImage";
            this.pictureBoxFullImage.Size = new System.Drawing.Size(1536, 864);
            this.pictureBoxFullImage.TabIndex = 0;
            this.pictureBoxFullImage.TabStop = false;
            // 
            // capture
            // 
            this.capture.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point);
            this.capture.Location = new System.Drawing.Point(1665, 12);
            this.capture.Name = "capture";
            this.capture.Size = new System.Drawing.Size(94, 49);
            this.capture.TabIndex = 1;
            this.capture.Text = "Capture";
            this.capture.UseVisualStyleBackColor = true;
            this.capture.Click += new System.EventHandler(this.capture_Click);
            // 
            // pictureBoxCrop
            // 
            this.pictureBoxCrop.Location = new System.Drawing.Point(12, 981);
            this.pictureBoxCrop.Name = "pictureBoxCrop";
            this.pictureBoxCrop.Size = new System.Drawing.Size(425, 77);
            this.pictureBoxCrop.TabIndex = 2;
            this.pictureBoxCrop.TabStop = false;
            // 
            // pictureBoxBorders
            // 
            this.pictureBoxBorders.Location = new System.Drawing.Point(633, 981);
            this.pictureBoxBorders.Name = "pictureBoxBorders";
            this.pictureBoxBorders.Size = new System.Drawing.Size(400, 77);
            this.pictureBoxBorders.TabIndex = 3;
            this.pictureBoxBorders.TabStop = false;
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1582, 1153);
            this.Controls.Add(this.pictureBoxBorders);
            this.Controls.Add(this.pictureBoxCrop);
            this.Controls.Add(this.capture);
            this.Controls.Add(this.pictureBoxFullImage);
            this.Name = "Form1";
            this.Text = "Roguelike Companion";
            this.WindowState = System.Windows.Forms.FormWindowState.Maximized;
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxFullImage)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxCrop)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxBorders)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.PictureBox pictureBoxFullImage;
        private System.Windows.Forms.Button capture;
        private System.Windows.Forms.PictureBox pictureBoxCrop;
        private System.Windows.Forms.PictureBox pictureBoxBorders;
    }
}

