import React, { useState } from 'react';
import { uploadVideo } from '../../api/api';
import '../../static/css/UploadVideo.css';

const UploadVideo = () => {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [file, setFile] = useState(null);
    const [message, setMessage] = useState('');

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        const allowedFormats = ['video/mp4', 'video/x-matroska'];

        if (selectedFile && allowedFormats.includes(selectedFile.type)) {
            setFile(selectedFile);
            setMessage('');
        } else {
            setMessage('Only .mp4 and .mkv video files are allowed.');
            e.target.value = null;
            setFile(null);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        try {
            await uploadVideo({ title, description, file });
            setMessage('Video uploaded successfully!');
            setTitle('');
            setDescription('');
            setFile(null);
            document.getElementById('file').value = '';
        } catch (error) {
            setMessage(`Error: ${error.message}`);
        }
    };

    return (
        <div className="upload-video-container">
            <h2>Upload Video</h2>
            <form onSubmit={handleSubmit} className="upload-form">
                <div className="form-group">
                    <label htmlFor="title">Video Title</label>
                    <input
                        type="text"
                        id="title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="Enter video title"
                        required
                        maxLength={100} // Limit title to 100 characters
                    />
                    <small>{title.length} / 100</small> {/* Character count display */}
                </div>

                <div className="form-group">
                    <label htmlFor="description">Description</label>
                    <textarea
                        id="description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        placeholder="Enter video description"
                        required
                        maxLength={250} // Limit description to 500 characters
                    />
                    <small>{description.length} / 250</small> {/* Character count display */}
                </div>

                <div className="form-group">
                    <label htmlFor="file">Video File</label>
                    <input
                        type="file"
                        id="file"
                        accept=".mp4,.mkv"
                        onChange={handleFileChange}
                        required
                    />
                </div>

                <button type="submit" className="submit-btn">Upload Video</button>
            </form>

            {message && (
                <p className={message.startsWith('Error') ? 'error-message' : 'success-message'}>
                    {message}
                </p>
            )}
        </div>
    );
};

export default UploadVideo;