package com.polly.visuals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.polly.R;

public class TermsOfService extends Fragment {
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.termsofservice, container, false);
        EditText terms = new EditText(getContext());
        terms.setText("Website Terms and Conditions of Use\n" +
                "1. Terms\n" +
                "By using this App, you are agreeing to be bound by these App Terms and Conditions of Use and agree that you are responsible for the agreement with any applicable local laws. If you disagree with any of these terms, you are prohibited from accessing this site. The materials contained in this Website are protected by copyright and trade mark law.\n" +
                "\n" +
                "2. Use License\n" +
                "Permission is granted to temporarily download one copy of the materials on the Polly App for personal, non-commercial transitory viewing only. This is the grant of a license, not a transfer of title, and under this license you may not:\n" +
                "\n" +
                "modify or copy the materials;\n" +
                "use the materials for any commercial purpose or for any public display;\n" +
                "attempt to reverse engineer any software contained in the Polly App;\n" +
                "remove any copyright or other proprietary notations from the materials; or\n" +
                "transferring the materials to another person or \"mirror\" the materials on any other server.\n" +
                "This will let Polly to terminate upon violations of any of these restrictions. Upon termination, your viewing right will also be terminated and you should destroy any downloaded materials in your possession whether it is printed or electronic format.\n" +
                "\n" +
                "3. Disclaimer\n" +
                "All the materials on the Polly App are provided \"as is\". Polly makes no warranties, may it be expressed or implied, therefore negates all other warranties. Furthermore, Polly does not make any representations concerning the accuracy or reliability of the use of the materials on its App or otherwise relating to such materials or any sites linked to this App.\n" +
                "\n" +
                "4. Limitations\n" +
                "Polly or its suppliers will not be hold accountable for any damages that will arise with the use or inability to use the materials on the Polly App, even if Polly or an authorize representative of this Website has been notified, orally or written, of the possibility of such damage. Some jurisdiction does not allow limitations on implied warranties or limitations of liability for incidental damages, these limitations may not apply to you.\n" +
                "\n" +
                "5. Revisions and Errata\n" +
                "The materials appearing on the Polly App may include technical, typographical, or photographic errors. Polly will not promise that any of the materials on this App are accurate, complete, or current. Polly may change the materials contained on its App at any time without notice. Polly does not make any commitment to update the materials.\n" +
                "\n" +
                "6. Links\n" +
                "Polly has not reviewed all of the sites linked to its App and is not responsible for the contents of any such linked site. The presence of any link does not imply endorsement by Polly of the site. The use of any linked website is at the user’s own risk.\n" +
                "\n" +
                "7. Site Terms of Use Modifications\n" +
                "Polly may revise these Terms of Use for its Website at any time without prior notice. By using this App, you are agreeing to be bound by the current version of these Terms and Conditions of Use.\n" +
                "\n" +
                "8. Your Privacy\n" +
                "Please read our Privacy Policy.\n" +
                "\n" +
                "9. Governing Law\n" +
                "Any claim related to the Polly App shall be governed by the laws of de without regards to its conflict of law provisions.");
        ((LinearLayout) root.findViewById(R.id.linear_layout_terms)).addView(terms);
        Button goBack = root.findViewById(R.id.goback_terms);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.signupFragment);
            }
        });
        return root;
    }
}
