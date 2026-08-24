using System.Windows;

namespace CyberLearningOS.Windows;

public partial class NewTopicWindow : Window
{
    public NewTopicWindow() => InitializeComponent();

    public string TopicTitle => TopicBox.Text.Trim();
    public string Purpose => PurposeBox.Text.Trim();
    public string Capability => CapabilityBox.Text.Trim();

    private void Start_Click(object sender, RoutedEventArgs e)
    {
        if (string.IsNullOrWhiteSpace(TopicTitle) || string.IsNullOrWhiteSpace(Purpose) ||
            string.IsNullOrWhiteSpace(Capability))
        {
            ErrorText.Text = "Topic, purpose and capability are all required.";
            return;
        }
        DialogResult = true;
    }

    private void Cancel_Click(object sender, RoutedEventArgs e) => DialogResult = false;
}
