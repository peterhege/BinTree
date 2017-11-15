
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024, 
    maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
@WebServlet(name = "MyServlet", urlPatterns = {"/MyServlet"})

public class MyServlet extends HttpServlet {

    int kliensek;
    int getek;
    int postok;
    
    //@Override
    public void init(){
        kliensek = 0;
        getek = 0;
        postok = 0;
    }   
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        //response.setContentType("image/png");
        try (PrintWriter out = response.getWriter()) {
            
            BinTree tree = new BinTree();
            
            String method = request.getMethod();
            
            if (method.equals("POST")) {

                Part myFile = request.getPart("myFile");

                InputStream contentStream = myFile.getInputStream();
                int cc;
                char c;

                boolean kommentben = false;
                
                while((cc = contentStream.read()) != -1){

                    c = (char)cc;
                    
                    if (c == 0x3e){			// > karakter
                        kommentben = true;
                        continue;
                    }

                    if (c == 0x0a){			// újsor
                        kommentben = false;
                        continue;
                    }

                    if (kommentben)
                        continue;

                    if (c == 0x4e)		// N betű
                        continue;
                    
                    for(int j = 0; j < 8; ++j){

                        if((c & 0x80) == 0x80)
                            tree.write('1');
                        else
                            tree.write('0');
                        c <<= 1;
                    }
                }
            }
            else {
                String beerkezo = request.getQueryString().split("=")[1];
                
                for (int i = 0; i < beerkezo.length(); i++) {
                    
                    tree.write(beerkezo.charAt(i));
                }
            }
            
            BufferedImage img = new BufferedImage(400,400,BufferedImage.TYPE_INT_ARGB);
            String szoveg1 = "Szoras: " + tree.getSzoras();
            String szoveg2 = "Atlag: " + tree.getAtlag();
            String szoveg3 = "Melyseg: " + tree.getMelyseg();
            String szoveg4 = "Kliensek: " + kliensek;
            String szoveg5 = "DoGet: " + getek + "      DoPost: " + postok;
            
            Graphics2D g2d = img.createGraphics();
            g2d.setPaint(Color.blue);
            int fontSize = 20;
            g2d.setFont(new Font("Arial", Font.BOLD,fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            int x = img.getWidth() - fm.stringWidth("a") -5;
            int y = img.getHeight();
            
            g2d.drawString(szoveg1,fontSize,y - 300);
            g2d.drawString(szoveg2,fontSize,y - 250);
            g2d.drawString(szoveg3,fontSize,y - 200);
            g2d.drawString(szoveg4,fontSize,y - 150);
            g2d.drawString(szoveg5,fontSize,y - 100);
            g2d.dispose();
            String path = getServletContext().getRealPath("/")+"/";
            String filename = "src/img/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HH_mm_ss")) + ".png";
            ImageIO.write(img,"png", new File(path + filename));
            
            
            out.write("<div class=\"tree\">");
            out.write("<h2>The Tree</h2>");
            tree.writeOut(tree.getRoot(), out, "");
            /*out.println("<br>Mélység: "+tree.getMelyseg());
            out.println("<br>Átlag: " + tree.getAtlag());
            out.println("<br>Szórás: " + tree.getSzoras());*/
            out.write("</div>");
            
            out.write("<img src=\"" + filename + "\">");
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getek++;
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        postok++;
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
